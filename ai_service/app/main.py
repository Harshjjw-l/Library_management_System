"""
main.py

FastAPI service that exposes the AI recommendation engine over HTTP.

This file only connects HTTP requests to the existing functions. All algorithms
live in recommender.py and coverage.py. Every request must include its own
book list; nothing is loaded from a database or file here.

Run from the ai_service root:
    python -m app.main
or:
    uvicorn app.main:app --host 127.0.0.1 --port 8000
"""

import logging
from typing import Any

from fastapi import FastAPI, HTTPException

from app.coverage import calculate_coverage, recommend_supplementary_books
from app.recommender import recommend_books
from app.schemas import (
    BookInput,
    CoverageRequest,
    CoverageResponse,
    RecommendationRequest,
    RecommendationResponse,
    SupplementaryRequest,
    SupplementaryResponse,
)

logger = logging.getLogger("ai_service")

app = FastAPI(
    title="Syllabus-Aware Library Recommendation AI Service",
    description=(
        "TF-IDF + cosine similarity book ranking, exact-match syllabus coverage, "
        "and greedy set-cover supplementary recommendations."
    ),
    version="0.1.0",
)


def _books_to_dicts(books: list[BookInput]) -> list[dict[str, Any]]:
    """Convert validated BookInput objects into plain dicts for the core functions."""
    return [book.model_dump() for book in books]


@app.get("/health")
def health() -> dict[str, str]:
    """Simple check that the service is running."""
    return {"status": "ok", "service": "ai-recommendation-engine"}


@app.post("/recommend", response_model=RecommendationResponse)
def recommend(request: RecommendationRequest) -> RecommendationResponse:
    """Rank the supplied books by TF-IDF cosine similarity to the syllabus topics."""
    try:
        results = recommend_books(
            request.syllabus_topics,
            _books_to_dicts(request.books),
            top_n=request.top_n,
        )
    except Exception:
        logger.exception("Unexpected error in /recommend")
        raise HTTPException(status_code=500, detail="Internal error while ranking books.")
    return RecommendationResponse(recommendations=results)


@app.post("/coverage", response_model=CoverageResponse)
def coverage(request: CoverageRequest) -> CoverageResponse:
    """Calculate exact-match syllabus topic coverage for the supplied books."""
    try:
        result = calculate_coverage(request.syllabus_topics, _books_to_dicts(request.books))
    except Exception:
        logger.exception("Unexpected error in /coverage")
        raise HTTPException(status_code=500, detail="Internal error while calculating coverage.")
    return CoverageResponse(**result)


@app.post("/supplementary", response_model=SupplementaryResponse)
def supplementary(request: SupplementaryRequest) -> SupplementaryResponse:
    """Recommend extra books that cover still-uncovered topics (greedy set cover)."""
    try:
        result = recommend_supplementary_books(
            request.syllabus_topics,
            _books_to_dicts(request.books),
            selected_book_ids=request.selected_book_ids,
            top_n=request.top_n,
        )
    except Exception:
        logger.exception("Unexpected error in /supplementary")
        raise HTTPException(
            status_code=500, detail="Internal error while selecting supplementary books."
        )
    return SupplementaryResponse(**result)


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="127.0.0.1", port=8000)