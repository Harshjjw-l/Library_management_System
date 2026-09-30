# Member 3 API Test Data

Once the AI teammate exposes `POST /recommend`, use this request body in Postman.

```json
{
  "syllabus_topics": [
    "Search",
    "BFS",
    "DFS",
    "A*",
    "Minimax",
    "Alpha-Beta Pruning",
    "Knowledge Representation",
    "Propositional Logic",
    "Predicate Logic",
    "Bayesian Networks",
    "Decision Trees",
    "Neural Networks",
    "NLP"
  ],
  "books": [
    {
      "id": 1,
      "title": "AI Fundamentals",
      "topics": ["Search", "BFS", "DFS", "A*", "Knowledge Representation"]
    },
    {
      "id": 3,
      "title": "Game AI and Adversarial Search",
      "topics": ["Minimax", "Alpha-Beta Pruning", "Game Trees", "Search"]
    },
    {
      "id": 4,
      "title": "Knowledge Representation and Reasoning",
      "topics": ["Knowledge Representation", "Propositional Logic", "Predicate Logic"]
    },
    {
      "id": 7,
      "title": "Deep Learning Essentials",
      "topics": ["Neural Networks", "Backpropagation", "Classification"]
    },
    {
      "id": 8,
      "title": "Natural Language Processing",
      "topics": ["NLP", "Text Preprocessing", "Classification"]
    }
  ]
}
```

## Manual test cases

1. Full syllabus above -> should return multiple relevant books and identify remaining topics for the primary book.
2. Syllabus `["BFS", "DFS"]` -> graph/search books should rank highly.
3. Syllabus `["Minimax", "Alpha-Beta Pruning"]` -> Game AI resources should rank highly.
4. Syllabus `["NLP"]` -> NLP resources should rank highly.
5. Syllabus with a topic not present in any book -> service should report it as uncovered rather than inventing a resource.
