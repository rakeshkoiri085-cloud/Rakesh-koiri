package com.example.data.repository

data class Flashcard(
    val id: String,
    val subject: String,
    val question: String,
    val answer: String,
    val keyConcept: String
)

data class StudyTopic(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val flashcards: List<Flashcard>
)

class StudyRepository {
    fun getPreloadedTopics(): List<StudyTopic> {
        return listOf(
            StudyTopic(
                id = "cs_algorithms",
                title = "Data Structures & Time Complexity",
                category = "Computer Science",
                description = "Master Big-O notation, binary search, trees, and hash tables.",
                flashcards = listOf(
                    Flashcard(
                        id = "fc_1",
                        subject = "Algorithms",
                        question = "What is the average and worst-case time complexity of QuickSort?",
                        answer = "Average: O(N log N). Worst case: O(N²) when pivot selection is consistently poor.",
                        keyConcept = "Divide & Conquer"
                    ),
                    Flashcard(
                        id = "fc_2",
                        subject = "Data Structures",
                        question = "What ensures O(1) average lookup in a Hash Table?",
                        answer = "A uniform hashing function with collision resolution strategies (Chaining or Open Addressing).",
                        keyConcept = "Hashing"
                    ),
                    Flashcard(
                        id = "fc_3",
                        subject = "Algorithms",
                        question = "When should you prefer Breadth-First Search (BFS) over Depth-First Search (DFS)?",
                        answer = "When finding the shortest path on an unweighted graph, or when the target is close to the root.",
                        keyConcept = "Graph Traversal"
                    )
                )
            ),
            StudyTopic(
                id = "ai_foundations",
                title = "Neural Networks & Backpropagation",
                category = "Artificial Intelligence",
                description = "Core deep learning concepts, loss functions, and activation layers.",
                flashcards = listOf(
                    Flashcard(
                        id = "fc_4",
                        subject = "Deep Learning",
                        question = "What is the primary role of an Activation Function (e.g. ReLU)?",
                        answer = "To introduce non-linearity into the network, enabling it to learn complex arbitrary representations.",
                        keyConcept = "Non-linearity"
                    ),
                    Flashcard(
                        id = "fc_5",
                        subject = "Optimization",
                        question = "What mathematical theorem underpins Backpropagation?",
                        answer = "The Calculus Chain Rule, computing partial derivatives of the loss with respect to every weight.",
                        keyConcept = "Chain Rule"
                    )
                )
            ),
            StudyTopic(
                id = "math_stats",
                title = "Probability & Bayes' Theorem",
                category = "Mathematics",
                description = "Essential statistics for machine learning and scientific reasoning.",
                flashcards = listOf(
                    Flashcard(
                        id = "fc_6",
                        subject = "Statistics",
                        question = "State Bayes' Theorem formula and concept.",
                        answer = "P(A|B) = [P(B|A) * P(A)] / P(B). It updates prior belief P(A) after observing new evidence B.",
                        keyConcept = "Conditional Probability"
                    )
                )
            )
        )
    }
}
