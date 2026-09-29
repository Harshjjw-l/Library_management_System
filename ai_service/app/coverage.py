"""Coverage analysis and greedy set-cover supplementary selection."""
"""
coverage.py

Syllabus coverage analysis and greedy set-cover book selection.

Coverage here means EXACT match of canonical (normalized) topics. Similarity
scores are never used as proof that a topic is covered.

Independent of FastAPI, the database, and the frontend.

Main functions:
    calculate_coverage(syllabus_topics, books)
    recommend_supplementary_books(syllabus_topics, books, selected_book_ids=None, top_n=5)
"""

from typing import Any

from app.preprocessing import normalize_topics

# Reuse the same book validation as recommender.py so both files treat
# invalid books identically (no duplicated validation logic).
from app.recommender import _prepare_books


def _percentage(covered: int, total: int) -> float:
    """Return covered/total as a percentage rounded to 2 decimals (0.0 if total is 0)."""
    if total <= 0:
        return 0.0
    return round(covered / total * 100, 2)


def calculate_coverage(syllabus_topics: Any, books: Any) -> dict[str, Any]:
    """Calculate which syllabus topics are covered by the given books.

    A topic is covered if at least one valid book lists the same canonical topic.

    Returns:
        {
            "syllabus_topics": [...],      # normalized, de-duplicated
            "covered_topics": [...],       # in syllabus order
            "missing_topics": [...],       # in syllabus order
            "covered_count": int,
            "total_count": int,
            "coverage_percentage": float,  # 0 to 100
            "explanation": str,
        }
    """
    syllabus = normalize_topics(syllabus_topics)
    valid_books = _prepare_books(books)

    # Every canonical topic offered by at least one book.
    available_topics: set[str] = set()
    for book in valid_books:
        available_topics.update(book["topics"])

    covered = [topic for topic in syllabus if topic in available_topics]
    missing = [topic for topic in syllabus if topic not in available_topics]
    percentage = _percentage(len(covered), len(syllabus))

    if not syllabus:
        explanation = "No valid syllabus topics were provided."
    elif not valid_books:
        explanation = "No valid books were provided, so no topics are covered."
    else:
        explanation = (
            f"{len(covered)} of {len(syllabus)} syllabus topics are covered "
            f"by exact topic matches in {len(valid_books)} book(s)."
        )

    return {
        "syllabus_topics": syllabus,
        "covered_topics": covered,
        "missing_topics": missing,
        "covered_count": len(covered),
        "total_count": len(syllabus),
        "coverage_percentage": percentage,
        "explanation": explanation,
    }


def recommend_supplementary_books(
    syllabus_topics: Any,
    books: Any,
    selected_book_ids: Any = None,
    top_n: int = 5,
) -> dict[str, Any]:
    """Pick books that cover the most still-uncovered topics (greedy set cover).

    Steps:
      1. Topics already covered by the selected books are treated as covered.
      2. Repeatedly choose the unselected book that covers the MOST currently
         uncovered syllabus topics (ties: title A-Z, then id).
      3. Stop when everything is covered, no book adds new coverage, or
         top_n books have been chosen.

    Greedy is a practical approximation; it is not guaranteed to use the
    smallest possible number of books.

    Returns:
        {
            "syllabus_topics": [...],
            "already_covered_topics": [...],
            "recommendations": [
                {
                    "id": ..., "title": "...",
                    "new_topics_covered": [...],
                    "new_topics_count": int,
                    "explanation": "...",
                }
            ],
            "remaining_uncovered_topics": [...],
            "coverage_percentage_before": float,
            "coverage_percentage_after": float,
            "explanation": str,
        }
    """
    syllabus = normalize_topics(syllabus_topics)
    valid_books = _prepare_books(books)

    # Selected IDs are compared as strings so 1 and "1" are treated the same.
    if isinstance(selected_book_ids, (list, tuple, set)):
        selected_ids = {str(book_id) for book_id in selected_book_ids if book_id is not None}
    else:
        selected_ids = set()

    max_picks = top_n if isinstance(top_n, int) and not isinstance(top_n, bool) and top_n > 0 else 0

    # Topics already covered by books the student has selected.
    selected_topics: set[str] = set()
    for book in valid_books:
        if str(book["id"]) in selected_ids:
            selected_topics.update(book["topics"])

    already_covered = [topic for topic in syllabus if topic in selected_topics]
    uncovered = [topic for topic in syllabus if topic not in selected_topics]
    initially_uncovered_count = len(uncovered)

    used_ids = set(selected_ids)  # also blocks duplicate recommendations
    recommendations: list[dict[str, Any]] = []

    while uncovered and len(recommendations) < max_picks:
        # Each option is (book, uncovered topics that book would add).
        options = []
        for book in valid_books:
            if str(book["id"]) in used_ids:
                continue
            book_topics = set(book["topics"])
            gain = [topic for topic in uncovered if topic in book_topics]
            if gain:
                options.append((book, gain))

        if not options:
            break  # no remaining book adds new coverage

        best_book, best_gain = min(
            options,
            key=lambda option: (-len(option[1]), option[0]["title"].lower(), str(option[0]["id"])),
        )

        used_ids.add(str(best_book["id"]))
        gained = set(best_gain)
        uncovered = [topic for topic in uncovered if topic not in gained]

        recommendations.append(
            {
                "id": best_book["id"],
                "title": best_book["title"],
                "new_topics_covered": best_gain,
                "new_topics_count": len(best_gain),
                "explanation": (
                    f"Adds {len(best_gain)} new syllabus topic(s) not yet covered: "
                    f"{', '.join(best_gain)}."
                ),
            }
        )

    total = len(syllabus)
    before = _percentage(total - initially_uncovered_count, total)
    after = _percentage(total - len(uncovered), total)

    if not syllabus:
        explanation = "No valid syllabus topics were provided."
    elif initially_uncovered_count == 0:
        explanation = "All syllabus topics are already covered by the selected books."
    elif recommendations and not uncovered:
        explanation = (
            f"{len(recommendations)} book(s) recommended; together with the selected "
            "books they cover all syllabus topics."
        )
    elif recommendations:
        explanation = (
            f"{len(recommendations)} book(s) recommended; {len(uncovered)} topic(s) remain "
            "uncovered (no further book adds coverage, or the top_n limit was reached)."
        )
    else:
        explanation = "No available book covers any of the remaining uncovered topics."

    return {
        "syllabus_topics": syllabus,
        "already_covered_topics": already_covered,
        "recommendations": recommendations,
        "remaining_uncovered_topics": uncovered,
        "coverage_percentage_before": before,
        "coverage_percentage_after": after,
        "explanation": explanation,
    }