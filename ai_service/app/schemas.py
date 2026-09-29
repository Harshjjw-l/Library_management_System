"""
schemas.py

Pydantic v2 request/response models for the AI service API.

Field names match the dictionaries returned by recommender.py and coverage.py.
The API contract is provisional, so all models live here in one place and are
easy to adjust.
"""

from pydantic import BaseModel, Field

# Book IDs may be numbers or strings depending on the caller's database.
BookId = int | str


# ---------------------------------------------------------------------------
# Shared input model
# ---------------------------------------------------------------------------
class BookInput(BaseModel):
    """One library book supplied by the caller."""

    id: BookId
    title: str | None = None
    topics: list[str] = Field(default_factory=list)


# ---------------------------------------------------------------------------
# Recommendation (recommender.recommend_books)
# ---------------------------------------------------------------------------
class RecommendationRequest(BaseModel):
    """Request for ranked book recommendations."""

    syllabus_topics: list[str] = Field(default_factory=list)
    books: list[BookInput] = Field(default_factory=list)
    top_n: int = Field(default=5, ge=1, le=100)


class RecommendedBook(BaseModel):
    """One ranked book."""

    id: BookId
    title: str
    similarity_score: float
    matched_topics: list[str]


class RecommendationResponse(BaseModel):
    """Ranked books, best first."""

    recommendations: list[RecommendedBook]
    score_note: str = (
        "similarity_score is a TF-IDF cosine similarity (0 to 1) used for ranking. "
        "It is not a probability or a measure of learning quality."
    )


# ---------------------------------------------------------------------------
# Coverage (coverage.calculate_coverage)
# ---------------------------------------------------------------------------
class CoverageRequest(BaseModel):
    """Request to measure how much of the syllabus the given books cover."""

    syllabus_topics: list[str] = Field(default_factory=list)
    books: list[BookInput] = Field(default_factory=list)


class CoverageResponse(BaseModel):
    """Exact-match coverage of the syllabus."""

    syllabus_topics: list[str]
    covered_topics: list[str]
    missing_topics: list[str]
    covered_count: int
    total_count: int
    coverage_percentage: float
    explanation: str


# ---------------------------------------------------------------------------
# Supplementary books (coverage.recommend_supplementary_books)
# ---------------------------------------------------------------------------
class SupplementaryRequest(BaseModel):
    """Request for extra books that cover still-uncovered syllabus topics."""

    syllabus_topics: list[str] = Field(default_factory=list)
    books: list[BookInput] = Field(default_factory=list)
    selected_book_ids: list[BookId] | None = None
    top_n: int = Field(default=5, ge=1, le=100)


class SupplementaryBook(BaseModel):
    """One supplementary book chosen by greedy set cover."""

    id: BookId
    title: str
    new_topics_covered: list[str]
    new_topics_count: int
    explanation: str


class SupplementaryResponse(BaseModel):
    """Supplementary picks plus before/after coverage."""

    syllabus_topics: list[str]
    already_covered_topics: list[str]
    recommendations: list[SupplementaryBook]
    remaining_uncovered_topics: list[str]
    coverage_percentage_before: float
    coverage_percentage_after: float
    explanation: str
    