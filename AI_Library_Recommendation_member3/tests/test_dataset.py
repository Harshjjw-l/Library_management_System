import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOKS = json.loads((ROOT / "data" / "books.json").read_text(encoding="utf-8"))
SYLLABUS = json.loads((ROOT / "data" / "syllabus_demo.json").read_text(encoding="utf-8"))
ALIASES = json.loads((ROOT / "data" / "topic_aliases.json").read_text(encoding="utf-8"))


def test_books_have_required_fields():
    assert len(BOOKS) >= 10
    ids = [book["id"] for book in BOOKS]
    assert len(ids) == len(set(ids))

    for book in BOOKS:
        assert isinstance(book["id"], int)
        assert book["title"]
        assert isinstance(book["topics"], list)
        assert book["topics"]


def test_syllabus_topics_are_present():
    assert SYLLABUS["course"] == "Artificial Intelligence"
    assert len(SYLLABUS["topics"]) >= 10

    all_book_topics = {topic for book in BOOKS for topic in book["topics"]}
    missing = set(SYLLABUS["topics"]) - all_book_topics

    assert not missing, f"Syllabus topics absent from all books: {sorted(missing)}"


def test_topic_aliases_have_canonical_topics():
    syllabus_topics = set(SYLLABUS["topics"])
    assert syllabus_topics.issubset(ALIASES.keys())

    for canonical, aliases in ALIASES.items():
        assert canonical
        assert canonical in aliases


def test_demo_primary_book_has_partial_coverage():
    syllabus_topics = set(SYLLABUS["topics"])
    book = next(book for book in BOOKS if book["id"] == 1)

    matched = syllabus_topics.intersection(book["topics"])
    coverage = len(matched) / len(syllabus_topics)

    assert 0 < coverage < 1
    assert "Minimax" in syllabus_topics - matched
    assert "NLP" in syllabus_topics - matched


def test_supplementary_books_can_add_missing_topics():
    syllabus_topics = set(SYLLABUS["topics"])
    primary = next(book for book in BOOKS if book["id"] == 1)

    remaining = syllabus_topics - set(primary["topics"])
    additions = set()

    for book in BOOKS:
        if book["id"] == primary["id"]:
            continue
        new_topics = set(book["topics"]).intersection(remaining)
        if new_topics:
            additions.update(new_topics)

    assert additions
    assert additions.issubset(remaining)
