"""Add unit tests for ranking, coverage, and supplementary recommendations here."""
"""
Automated tests for the AI recommendation engine.

Run from the ai_service root:
    python -m pytest -v

All data is small and in-memory. No database or network is used.
"""

import sys
from pathlib import Path

# Make "import app" work no matter how pytest is launched.
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))

import pytest
from fastapi.testclient import TestClient

from app.coverage import calculate_coverage, recommend_supplementary_books
from app.main import app
from app.preprocessing import normalize_topic, normalize_topics
from app.recommender import recommend_books

BOOKS = [
    {"id": 1, "title": "Intro to AI", "topics": ["BFS", "DFS", "Search"]},
    {"id": 2, "title": "Game AI", "topics": ["Minimax", "Game Trees"]},
    {"id": 3, "title": "Graph Algorithms", "topics": ["Breadth First Search", "Graphs"]},
    {"id": 4, "title": "Databases", "topics": ["SQL", "Normalization"]},
]

SYLLABUS = ["BFS", "DFS", "Minimax"]


@pytest.fixture
def client() -> TestClient:
    return TestClient(app)


# ---------------------------------------------------------------- preprocessing
def test_alias_normalization():
    assert normalize_topic("BFS") == "breadth first search"
    assert normalize_topic("  Depth-First Search ") == "depth first search"
    assert normalize_topic("A*") == "a star"
    assert normalize_topic("A-Star") == "a star"
    assert normalize_topic("KNN") == "k nearest neighbors"


def test_normalize_topics_dedupes_and_skips_invalid():
    result = normalize_topics(["DFS", "Depth First Search", "  ", None, "Minimax!"])
    assert result == ["depth first search", "minimax"]


# ------------------------------------------------------------------ recommender
def test_recommend_ranking_and_matched_topics():
    results = recommend_books(SYLLABUS, BOOKS)
    ids = [r["id"] for r in results]

    assert ids[0] == 1  # matches two syllabus topics
    assert 4 not in ids  # unrelated book is never returned
    assert results[0]["matched_topics"] == ["breadth first search", "depth first search"]

    scores = [r["similarity_score"] for r in results]
    assert scores == sorted(scores, reverse=True)
    assert all(0.0 <= s <= 1.0 for s in scores)


def test_recommend_respects_top_n():
    assert len(recommend_books(SYLLABUS, BOOKS, top_n=1)) == 1


def test_recommend_unrelated_topics_returns_empty():
    assert recommend_books(["Quantum Computing"], BOOKS) == []


def test_recommend_empty_inputs():
    assert recommend_books([], BOOKS) == []
    assert recommend_books(SYLLABUS, []) == []


# --------------------------------------------------------------------- coverage
def test_calculate_coverage():
    result = calculate_coverage(["BFS", "DFS", "Minimax", "Quantum Computing"], BOOKS)
    assert result["covered_count"] == 3
    assert result["total_count"] == 4
    assert result["coverage_percentage"] == 75.0
    assert result["missing_topics"] == ["quantum computing"]


def test_calculate_coverage_empty_syllabus():
    result = calculate_coverage([], BOOKS)
    assert result["total_count"] == 0
    assert result["coverage_percentage"] == 0.0


# ---------------------------------------------------------------- supplementary
def test_supplementary_excludes_selected_and_improves_coverage():
    result = recommend_supplementary_books(SYLLABUS, BOOKS, selected_book_ids=[1])
    ids = [r["id"] for r in result["recommendations"]]

    assert ids == [2]  # book 1 is already selected; book 3 adds nothing new
    assert 1 not in ids
    assert result["recommendations"][0]["new_topics_covered"] == ["minimax"]
    assert result["remaining_uncovered_topics"] == []
    assert result["coverage_percentage_after"] > result["coverage_percentage_before"]
    assert result["coverage_percentage_after"] == 100.0


def test_supplementary_no_duplicate_recommendations():
    result = recommend_supplementary_books(SYLLABUS, BOOKS)
    ids = [r["id"] for r in result["recommendations"]]
    assert len(ids) == len(set(ids))


# -------------------------------------------------------------------------- API
def test_health(client):
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_api_recommend(client):
    response = client.post(
        "/recommend", json={"syllabus_topics": SYLLABUS, "books": BOOKS, "top_n": 2}
    )
    assert response.status_code == 200
    recs = response.json()["recommendations"]
    assert len(recs) == 2
    assert recs[0]["id"] == 1
    assert set(recs[0]) == {"id", "title", "similarity_score", "matched_topics"}


def test_api_recommend_empty_syllabus_returns_empty_list(client):
    response = client.post("/recommend", json={"syllabus_topics": [], "books": BOOKS})
    assert response.status_code == 200
    assert response.json()["recommendations"] == []


def test_api_coverage(client):
    response = client.post(
        "/coverage",
        json={"syllabus_topics": ["BFS", "DFS", "Minimax", "Quantum Computing"], "books": BOOKS},
    )
    assert response.status_code == 200
    body = response.json()
    assert body["coverage_percentage"] == 75.0
    assert body["missing_topics"] == ["quantum computing"]


def test_api_supplementary(client):
    response = client.post(
        "/supplementary",
        json={"syllabus_topics": SYLLABUS, "books": BOOKS, "selected_book_ids": [1]},
    )
    assert response.status_code == 200
    body = response.json()
    assert [r["id"] for r in body["recommendations"]] == [2]
    assert body["coverage_percentage_after"] == 100.0


def test_api_rejects_invalid_requests(client):
    # A book without an "id" fails validation.
    bad_book = {"syllabus_topics": ["BFS"], "books": [{"title": "No id", "topics": ["BFS"]}]}
    assert client.post("/recommend", json=bad_book).status_code == 422

    # top_n must be at least 1.
    bad_top_n = {"syllabus_topics": ["BFS"], "books": BOOKS, "top_n": 0}
    assert client.post("/recommend", json=bad_top_n).status_code == 422