"""
preprocessing.py

Text cleaning and topic normalization for the AI recommendation engine.

Uses only the Python standard library. It is independent of FastAPI, the
database, and the frontend, so recommender.py and coverage.py can import it:

    from app.preprocessing import normalize_topic, normalize_topics

To add a new alias, add it to TOPIC_ALIASES below. Aliases are cleaned with the
same rules as user input, so write them naturally (e.g. "Breadth-First Search").
"""

import re
import unicodedata
from typing import Any, Dict, Iterable, List

# ---------------------------------------------------------------------------
# Alias table: canonical topic name -> list of alternative spellings.
# Canonical names are stored in cleaned form (lowercase, single spaces).
# To extend, add a new entry or append to an existing list.
# ---------------------------------------------------------------------------
TOPIC_ALIASES: Dict[str, List[str]] = {
    "breadth first search": [
        "bfs",
        "breadth-first search",
        "breadth first traversal",
    ],
    "depth first search": [
        "dfs",
        "depth-first search",
        "depth first traversal",
    ],
    "k nearest neighbors": [
        "knn",
        "k-nn",
        "k nearest neighbours",
        "k-nearest neighbors",
        "k-nearest neighbours",
    ],
    "a star": [
        "a*",
        "a-star",
        "astar",
        "a* search",
        "a star search",
    ],
}


def _clean_text(text: str) -> str:
    """Lowercase, trim, and normalize whitespace and punctuation.

    Rules:
    - Unicode is normalized (NFKC) and lowercased.
    - "*" becomes " star " so that "A*" turns into "a star".
    - Punctuation, hyphens, and underscores become spaces.
    - "+" and "#" are kept so topics like "C++" and "C#" stay distinct.
    - Repeated whitespace is collapsed to a single space.

    Returns an empty string if nothing meaningful is left.
    """
    text = unicodedata.normalize("NFKC", text).lower()
    text = text.replace("*", " star ")
    text = re.sub(r"[^\w\s+#]|_", " ", text)
    text = " ".join(text.split())

    # Reject strings with no letters or digits (e.g. "---", "+").
    if not any(ch.isalnum() for ch in text):
        return ""
    return text


def _build_alias_lookup(aliases: Dict[str, List[str]]) -> Dict[str, str]:
    """Build a flat lookup of cleaned alias -> cleaned canonical name."""
    lookup: Dict[str, str] = {}
    for canonical, alias_list in aliases.items():
        canonical_clean = _clean_text(canonical)
        if not canonical_clean:
            continue
        lookup[canonical_clean] = canonical_clean  # canonical maps to itself
        for alias in alias_list:
            alias_clean = _clean_text(alias)
            if alias_clean:
                lookup[alias_clean] = canonical_clean
    return lookup


# Built once at import time.
_ALIAS_TO_CANONICAL: Dict[str, str] = _build_alias_lookup(TOPIC_ALIASES)


def normalize_topic(topic: str) -> str:
    """Normalize one topic string to its canonical form.

    Steps: clean the text, then map known aliases to the canonical name.
    Unknown topics are returned cleaned but otherwise unchanged.

    Returns "" for empty, non-string, or punctuation-only input.
    """
    if not isinstance(topic, str):
        return ""
    cleaned = _clean_text(topic)
    if not cleaned:
        return ""
    return _ALIAS_TO_CANONICAL.get(cleaned, cleaned)


def normalize_topics(topics: Iterable[Any]) -> List[str]:
    """Normalize a list of topics and remove duplicates, keeping first-seen order.

    Empty or invalid entries (None, numbers, blank strings) are skipped.
    If `topics` is None, a string, or not iterable, an empty list is returned.
    """
    if topics is None or isinstance(topics, (str, bytes)):
        return []
    try:
        iterator = iter(topics)
    except TypeError:
        return []

    result: List[str] = []
    seen = set()
    for topic in iterator:
        normalized = normalize_topic(topic)
        if normalized and normalized not in seen:
            seen.add(normalized)
            result.append(normalized)
    return result