# Member 3 Dataset Package

This folder contains the demo dataset and validation material for the AI Library Recommendation Service.

## Purpose

The dataset is designed for:
- TF-IDF + cosine-similarity book ranking
- syllabus topic coverage calculation
- greedy set-cover supplementary-resource selection
- explainable recommendation output

## Files

- `books.json`: 15 demo library resources with normalized topic tags.
- `syllabus_demo.json`: 13 syllabus topics for the demonstration course.
- `topic_aliases.json`: common aliases used for topic normalization.
- `test_dataset.py`: validates dataset structure, topic consistency, and expected coverage behavior.

## Important

This is a manually curated demonstration dataset. It is not a claim that the books actually contain these topics. When the real library database is connected, these records should be replaced by verified library metadata.

## Integration

The Spring Boot/backend teammate can later map database book records to this same shape:

```json
{
  "id": 1,
  "title": "AI Fundamentals",
  "topics": ["Search", "BFS", "DFS", "A*"]
}
```

The AI service does not need direct database access.
