"""Book ranking logic: implement TF-IDF and cosine similarity here."""
"""
recommender.py

Ranks library books against syllabus topics using TF-IDF and cosine similarity.

Independent of FastAPI, the database, and the frontend. Depends only on
preprocessing.py and scikit-learn.

Main function:
    recommend_books(syllabus_topics, books, top_n=5)
"""

from typing import Any

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from app.preprocessing import normalize_topic, normalize_topics  # noqa: F401


def _topics_to_text(topics: list[str]) -> str:
    """Turn canonical topics into one text string for TF-IDF.

    Spaces inside a topic become underscores so each topic stays ONE token,
    e.g. ["breadth first search", "minimax"] -> "breadth_first_search minimax".
    """
    return " ".join(topic.replace(" ", "_") for topic in topics)


def _prepare_books(books: Any) -> list[dict[str, Any]]:
    """Validate raw book dictionaries and normalize their topics.

    A book is kept only if it is a dict, has an "id", and has at least one
    usable topic after normalization. Missing titles become "Untitled".
    """
    if not isinstance(books, (list, tuple)):
        return []

    prepared: list[dict[str, Any]] = []
    for book in books:
        if not isinstance(book, dict):
            continue

        book_id = book.get("id")
        if book_id is None:
            continue

        topics = normalize_topics(book.get("topics"))
        if not topics:
            continue

        title = book.get("title")
        if not isinstance(title, str) or not title.strip():
            title = "Untitled"

        prepared.append({"id": book_id, "title": title.strip(), "topics": topics})
    return prepared


def recommend_books(
    syllabus_topics: Any,
    books: Any,
    top_n: int = 5,
) -> list[dict[str, Any]]:
    """Rank books by TF-IDF cosine similarity to the syllabus topics.

    Args:
        syllabus_topics: List of syllabus topic strings (raw, un-normalized).
        books: List of dicts, each with "id", "title", and "topics".
        top_n: Maximum number of books to return.

    Returns:
        A list of dicts, best match first:
            {
                "id": ...,
                "title": "...",
                "similarity_score": 0.5980,        # cosine similarity, 0 to 1
                "matched_topics": ["breadth first search", ...],
            }
        Books that share no topic with the syllabus are NOT returned.
        Returns [] for empty syllabus, no valid books, no matches, or invalid top_n.
    """
    if isinstance(top_n, bool) or not isinstance(top_n, int) or top_n <= 0:
        return []

    syllabus = normalize_topics(syllabus_topics)
    if not syllabus:
        return []

    valid_books = _prepare_books(books)
    if not valid_books:
        return []

    # One text per book, plus the syllabus as the last document.
    book_texts = [_topics_to_text(book["topics"]) for book in valid_books]
    syllabus_text = _topics_to_text(syllabus)

    # Topics are already normalized, so we disable the default lowercasing and
    # word-splitting rules and just split on spaces.
    vectorizer = TfidfVectorizer(tokenizer=str.split, token_pattern=None, lowercase=False)
    matrix = vectorizer.fit_transform(book_texts + [syllabus_text])

    syllabus_vector = matrix[-1]
    book_matrix = matrix[:-1]
    similarities = cosine_similarity(syllabus_vector, book_matrix)[0]

    syllabus_set = set(syllabus)
    results: list[dict[str, Any]] = []
    for book, similarity in zip(valid_books, similarities):
        # Exact canonical matches only, listed in syllabus order.
        book_topic_set = set(book["topics"])
        matched = [topic for topic in syllabus if topic in book_topic_set]
        if not matched:
            continue  # no shared topic -> not a real match

        results.append(
            {
                "id": book["id"],
                "title": book["title"],
                "similarity_score": round(float(similarity), 4),
                "matched_topics": matched,
            }
        )

    # Highest score first; ties -> more matched topics, then title, then id.
    results.sort(
        key=lambda r: (
            -r["similarity_score"],
            -len(r["matched_topics"]),
            r["title"].lower(),
            str(r["id"]),
        )
    )
    return results[:top_n]