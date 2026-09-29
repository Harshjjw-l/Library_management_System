# AI-Based Syllabus-Aware Library Recommendation Engine

A standalone Python service that recommends library books for a student's
syllabus topics, measures how much of the syllabus the books cover, and
suggests extra books to fill the gaps. Every result is computed by the
algorithms in this module; nothing is hardcoded or invented.

## Purpose and architecture

The Java Spring Boot backend will later call this service over HTTP and send it
the syllabus topics and the book list. The service does not connect to MySQL or
the Java backend.

```
Caller (later: Spring Boot)  --HTTP/JSON-->  FastAPI (main.py)
                                                 |
                       +-------------------------+-------------------------+
                       |                         |                         |
                 recommender.py             coverage.py              preprocessing.py
              (TF-IDF + cosine)      (coverage + greedy set cover)   (topic normalization)
```

`main.py` only validates requests (using `schemas.py`) and calls the core
functions. The core functions know nothing about FastAPI, so they can be tested
and reused on their own.

## What the algorithms do

- **Topic normalization:** lowercases, trims, and cleans punctuation, and maps aliases to one
  canonical name (for example `BFS` and `Breadth-First Search` both become
  `breadth first search`). This makes topic matching consistent.
- **TF-IDF:** turns the syllabus and each book's topic list into numeric vectors. Topics that
  appear in fewer books get a higher weight, so rare topics count for more than common ones.
  Each canonical topic is treated as one token.
- **Cosine similarity:** compares the syllabus vector with each book vector. The score is between
  0 and 1 and is used to sort books. It is a relevance ranking, not a probability or a measure of
  learning quality. Scores depend on the books in the request, so compare them only within one response.
- **Coverage:** a syllabus topic is covered only if some book lists the same canonical topic
  (exact match). Similarity scores are never used as proof of coverage.
- **Greedy set cover:** repeatedly picks the unselected book that covers the most
  still-uncovered syllabus topics, until everything is covered, no book adds anything new, or
  `top_n` books are chosen. It is a practical approximation and is not guaranteed to use the fewest books.

## Folder structure

```
ai_service/
  app/
    __init__.py
    main.py            FastAPI endpoints
    schemas.py         Pydantic request/response models
    preprocessing.py   Topic cleaning and aliases
    recommender.py     TF-IDF + cosine similarity ranking
    coverage.py        Coverage + greedy set cover
  data/
    books.json         Sample dataset (not loaded by the API)
  tests/
    test_recommender.py
  requirements.txt
  README.md
```

## Setup (Windows PowerShell)

Run all commands from the `ai_service` folder.

```powershell
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

If PowerShell blocks activation, run this once and try again:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

Do not commit `.venv/` to Git.

## Run the server

```powershell
python -m app.main
```

Or:

```powershell
uvicorn app.main:app --host 127.0.0.1 --port 8000 --reload
```

The service runs at `http://127.0.0.1:8000`. Interactive API docs are at
`http://127.0.0.1:8000/docs`.

## API endpoints

Every request must include its own `books` list. `data/books.json` is only sample data for
local experiments.

### GET /health

```json
{ "status": "ok", "service": "ai-recommendation-engine" }
```

### POST /recommend

Request (`top_n` is optional, default 5, allowed 1 to 100):

```json
{
  "syllabus_topics": ["BFS", "Depth First Search", "Minimax"],
  "books": [
    { "id": 1, "title": "Introduction to AI", "topics": ["BFS", "DFS", "Search"] },
    { "id": 2, "title": "Artificial Intelligence", "topics": ["Minimax", "Game Trees"] }
  ],
  "top_n": 5
}
```

Response structure:

```json
{
  "recommendations": [
    {
      "id": 1,
      "title": "Introduction to AI",
      "similarity_score": 0.598,
      "matched_topics": ["breadth first search", "depth first search"]
    },
    {
      "id": 2,
      "title": "Artificial Intelligence",
      "similarity_score": 0.3495,
      "matched_topics": ["minimax"]
    }
  ],
  "score_note": "similarity_score is a TF-IDF cosine similarity (0 to 1) used for ranking. It is not a probability or a measure of learning quality."
}
```

Books that share no topic with the syllabus are not returned.

### POST /coverage

Request:

```json
{
  "syllabus_topics": ["BFS", "DFS", "Minimax"],
  "books": [
    { "id": 1, "title": "Introduction to AI", "topics": ["BFS", "DFS", "Search"] },
    { "id": 2, "title": "Artificial Intelligence", "topics": ["Minimax", "Game Trees"] }
  ]
}
```

Response structure:

```json
{
  "syllabus_topics": ["breadth first search", "depth first search", "minimax"],
  "covered_topics": ["breadth first search", "depth first search", "minimax"],
  "missing_topics": [],
  "covered_count": 3,
  "total_count": 3,
  "coverage_percentage": 100.0,
  "explanation": "3 of 3 syllabus topics are covered by exact topic matches in 2 book(s)."
}
```

### POST /supplementary

Request (`selected_book_ids` and `top_n` are optional):

```json
{
  "syllabus_topics": ["BFS", "DFS", "Minimax"],
  "books": [
    { "id": 1, "title": "Introduction to AI", "topics": ["BFS", "DFS", "Search"] },
    { "id": 2, "title": "Artificial Intelligence", "topics": ["Minimax", "Game Trees"] }
  ],
  "selected_book_ids": [1],
  "top_n": 5
}
```

Response structure:

```json
{
  "syllabus_topics": ["breadth first search", "depth first search", "minimax"],
  "already_covered_topics": ["breadth first search", "depth first search"],
  "recommendations": [
    {
      "id": 2,
      "title": "Artificial Intelligence",
      "new_topics_covered": ["minimax"],
      "new_topics_count": 1,
      "explanation": "Adds 1 new syllabus topic(s) not yet covered: minimax."
    }
  ],
  "remaining_uncovered_topics": [],
  "coverage_percentage_before": 66.67,
  "coverage_percentage_after": 100.0,
  "explanation": "1 book(s) recommended; together with the selected books they cover all syllabus topics."
}
```

### Calling the API from PowerShell

```powershell
$body = @'
{
  "syllabus_topics": ["BFS", "DFS", "Minimax"],
  "books": [
    { "id": 1, "title": "Introduction to AI", "topics": ["BFS", "DFS", "Search"] },
    { "id": 2, "title": "Artificial Intelligence", "topics": ["Minimax", "Game Trees"] }
  ]
}
'@
Invoke-RestMethod -Uri http://127.0.0.1:8000/recommend -Method Post -ContentType "application/json" -Body $body
```

### Status codes

- `200`: success. Empty syllabus, empty book list, or no matching books returns `200` with empty results.
- `422`: invalid request (for example a book without `id`, or `top_n` outside 1 to 100).
- `500`: unexpected internal error (details are logged on the server).

## Run the tests

```powershell
python -m pytest -v
```

Tests use small in-memory datasets and need no database or network.

## Current limitations

- Books are curated and tagged with topics by hand; there is no automatic topic tagging.
- There is no real PDF or syllabus text extraction. Syllabus topics must be supplied as a list.
- Matching is exact on canonical topics. A synonym is recognized only if it is listed in
  `TOPIC_ALIASES` in `preprocessing.py`.
- There is no database integration yet. The caller sends the books in every request.
- There is no feedback-based learning yet.
- Similarity scores depend on the books in each request and are not comparable across requests.