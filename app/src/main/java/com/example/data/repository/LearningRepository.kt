package com.example.data.repository

import com.example.data.local.LessonProgressDao
import com.example.data.local.LessonProgressEntity
import kotlinx.coroutines.flow.Flow

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class Lesson(
    val id: String,
    val levelNumber: Int,
    val title: String,
    val shortSummary: String,
    val explanation: String,
    val codeExample: String,
    val practicePrompt: String,
    val quiz: QuizQuestion,
    val miniProject: String
)

data class LearningLevel(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val lessons: List<Lesson>
)

class LearningRepository(
    private val lessonProgressDao: LessonProgressDao
) {
    val allProgress: Flow<List<LessonProgressEntity>> = lessonProgressDao.getAllLessonProgress()

    suspend fun markLessonCompleted(lessonId: String, levelNumber: Int, quizScore: Int) {
        lessonProgressDao.saveLessonProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                levelNumber = levelNumber,
                isCompleted = true,
                quizScore = quizScore,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetAllProgress() {
        lessonProgressDao.resetAllProgress()
    }

    fun getCurriculum(): List<LearningLevel> {
        return listOf(
            LearningLevel(
                levelNumber = 1,
                title = "Level 1 — AI Fundamentals",
                subtitle = "Foundations of Artificial Intelligence & Core Concepts",
                iconName = "Psychology",
                lessons = listOf(
                    Lesson(
                        id = "l1_what_is_ai",
                        levelNumber = 1,
                        title = "What is Artificial Intelligence?",
                        shortSummary = "Understanding intelligence, agents, perception, and decision making.",
                        explanation = """
Artificial Intelligence (AI) refers to systems designed to perceive their environment, learn from data, reason through constraints, and take purposeful actions to accomplish specific goals.

Key distinctions:
- **Narrow (Weak) AI**: Systems specialized in specific domains (e.g. computer vision, language translation, recommendation feeds). All modern AI today is Narrow AI!
- **General AI (AGI)**: Hypothetical systems possessing human-level cognitive flexibility across any intellectual task.
- **Superintelligence**: Theoretical future AI exceeding collective human cognitive capability.
                        """.trimIndent(),
                        codeExample = """
# Conceptual representation of an intelligent agent loop
class SimpleAgent:
    def __init__(self, goal: str):
        self.goal = goal

    def perceive_and_act(self, environment_state: dict) -> str:
        if environment_state.get("obstacle"):
            return "Rerouting path..."
        return "Proceeding toward goal: " + self.goal
                        """.trimIndent(),
                        practicePrompt = "Ask SUPER AI: 'Explain the difference between rule-based expert systems and machine learning with an example.'",
                        quiz = QuizQuestion(
                            question = "Which type of AI represents all existing modern production systems today?",
                            options = listOf("Artificial General Intelligence (AGI)", "Narrow (Weak) AI", "Superintelligence", "Quantum Consciousness"),
                            correctIndex = 1,
                            explanation = "All current AI models, including ChatGPT and Gemini, are Narrow AI specialized in pattern processing."
                        ),
                        miniProject = "Define the input, processing state, and output actions for an AI Thermostat agent."
                    ),
                    Lesson(
                        id = "l1_how_ai_works",
                        levelNumber = 1,
                        title = "How AI Works: Patterns, Data & Weights",
                        shortSummary = "How neural networks compute predictions through numerical parameters.",
                        explanation = """
At its core, AI operates through statistical pattern matching over high-dimensional vector spaces. 

Instead of an engineer manually writing `if/else` conditions for every rule, a machine learning algorithm adjusts millions or billions of mathematical weights through optimization algorithms like Gradient Descent until prediction error is minimized.
                        """.trimIndent(),
                        codeExample = """
# The fundamental linear unit: output = (input * weight) + bias
def neuron(input_value: float, weight: float, bias: float) -> float:
    activation = (input_value * weight) + bias
    # ReLU activation function: max(0, x)
    return max(0.0, activation)

print("Neuron Output:", neuron(2.5, 0.8, -0.5))
                        """.trimIndent(),
                        practicePrompt = "Ask SUPER AI: 'What is gradient descent in simple terms, using the analogy of a hiker walking down a foggy mountain?'",
                        quiz = QuizQuestion(
                            question = "What do machine learning models adjust during the training phase?",
                            options = listOf("Hardware voltages", "Mathematical weights and biases", "Screen refresh rates", "Memory RAM sticks"),
                            correctIndex = 1,
                            explanation = "Training optimizes weights and biases to reduce loss on target outputs."
                        ),
                        miniProject = "Trace how a spam detector assigns probability weights to words like 'lottery' or 'invoice'."
                    )
                )
            ),
            LearningLevel(
                levelNumber = 2,
                title = "Level 2 — Prompt Engineering",
                subtitle = "Mastering Context, Directives, Few-Shot & Reasoning",
                iconName = "Terminal",
                lessons = listOf(
                    Lesson(
                        id = "l2_prompt_structure",
                        levelNumber = 2,
                        title = "Anatomy of an Elite Prompt",
                        shortSummary = "Role, Context, Task, Constraints, and Output Format.",
                        explanation = """
Effective prompt engineering structures instructions into five essential pillars:
1. **Role**: Who the AI is acting as (e.g. 'Principal Cloud Architect').
2. **Context**: Background facts, target audience, and environment constraints.
3. **Specific Task**: Active verbs detailing exactly what to generate.
4. **Constraints**: What to avoid, negative bounds, word count or tone.
5. **Output Format**: Schema, Markdown tables, JSON, or bullet points.
                        """.trimIndent(),
                        codeExample = """
# Gold Standard Prompt Template:
[ROLE]: Senior Mobile Developer
[CONTEXT]: Building a high-performance Android Jetpack Compose app
[TASK]: Write an immutable StateFlow ViewModel for user authentication
[CONSTRAINTS]: No third-party DI frameworks; use clean Kotlin Coroutines
[OUTPUT FORMAT]: Kotlin code block followed by a 3-bullet explanation
                        """.trimIndent(),
                        practicePrompt = "Use the SUPER AI Prompt Generator tool to craft a prompt for summarizing textbook chapters.",
                        quiz = QuizQuestion(
                            question = "Which pillar prevents the AI from generating unwanted filler or incorrect formats?",
                            options = listOf("Preamble", "Constraints & Output Format", "Token size", "Random seed"),
                            correctIndex = 1,
                            explanation = "Explicit constraints and format specifications strictly govern model generation boundaries."
                        ),
                        miniProject = "Refactor a vague prompt ('Help me with my resume') into a 5-pillar structured prompt."
                    )
                )
            ),
            LearningLevel(
                levelNumber = 3,
                title = "Level 3 — Python for AI",
                subtitle = "Variables, Data Structures, Functions & Algorithms",
                iconName = "Code",
                lessons = listOf(
                    Lesson(
                        id = "l3_python_data_structures",
                        levelNumber = 3,
                        title = "Lists, Dictionaries & Vector Arrays",
                        shortSummary = "Essential Python primitives for structuring data and AI embeddings.",
                        explanation = """
Python is the lingua franca of AI due to its readability and rich ecosystem (NumPy, PyTorch, Pandas).

Key constructs:
- **Lists**: Ordered, mutable sequences `[1, 2, 3]`
- **Dictionaries**: Fast hash-map lookups `{"key": "value"}`
- **List Comprehensions**: Elegant, concise transformations `[x * 2 for x in items]`
                        """.trimIndent(),
                        codeExample = """
# Data transformation for AI tokens
raw_tokens = ["super", "ai", "platform", "learning", "code"]
token_lengths = {token: len(token) for token in raw_tokens if len(token) > 2}

print("Processed Token Map:", token_lengths)
                        """.trimIndent(),
                        practicePrompt = "Ask SUPER AI Coding Assistant: 'Show me how to filter a list of dictionary records by key in Python.'",
                        quiz = QuizQuestion(
                            question = "What Python collection type stores key-value associations with O(1) average lookup?",
                            options = listOf("List", "Dictionary (dict)", "Tuple", "String"),
                            correctIndex = 1,
                            explanation = "Python dictionaries use hash tables for near-instantaneous key lookups."
                        ),
                        miniProject = "Write a Python script that counts word frequencies from a user paragraph."
                    )
                )
            ),
            LearningLevel(
                levelNumber = 4,
                title = "Level 4 — Machine Learning",
                subtitle = "Datasets, Training, Testing & Supervised Learning",
                iconName = "Insights",
                lessons = listOf(
                    Lesson(
                        id = "l4_train_test_split",
                        levelNumber = 4,
                        title = "Training vs Testing: Avoiding Overfitting",
                        shortSummary = "How models generalize to unseen real-world data.",
                        explanation = """
A fundamental rule in ML is never evaluating a model on the exact data it was trained on. 

We split datasets (typically 80% Train, 20% Test). 
- **Underfitting**: The model is too simple to capture patterns.
- **Overfitting**: The model memorizes training noise and fails on new samples.
                        """.trimIndent(),
                        codeExample = """
# Conceptual train-test split
def train_test_split(dataset: list, test_ratio: float = 0.2):
    split_index = int(len(dataset) * (1 - test_ratio))
    train_set = dataset[:split_index]
    test_set = dataset[split_index:]
    return train_set, test_set

train, test = train_test_split(list(range(100)), 0.2)
print(f"Train size: {len(train)}, Test size: {len(test)}")
                        """.trimIndent(),
                        practicePrompt = "Ask SUPER AI: 'How do regularization techniques like L1 and L2 prevent overfitting in neural networks?'",
                        quiz = QuizQuestion(
                            question = "What occurs when a model scores 99% on training data but 40% on real-world test data?",
                            options = listOf("Underfitting", "Overfitting", "Data normalization", "Zero-shot transfer"),
                            correctIndex = 1,
                            explanation = "Overfitting occurs when a model memorizes specific training samples without learning generalized patterns."
                        ),
                        miniProject = "Design a simple dataset table with 4 input features to predict house prices."
                    )
                )
            ),
            LearningLevel(
                levelNumber = 5,
                title = "Level 5 — Generative AI & LLMs",
                subtitle = "Transformers, Tokens, Embeddings & Context Windows",
                iconName = "AutoAwesome",
                lessons = listOf(
                    Lesson(
                        id = "l5_tokens_and_embeddings",
                        levelNumber = 5,
                        title = "Tokens, Vectors & Semantic Space",
                        shortSummary = "How language models convert words into numerical vectors.",
                        explanation = """
Large Language Models do not read letters or words directly. Instead:
1. **Tokenization**: Text is sliced into token fragments (~4 characters per token).
2. **Embedding**: Each token is projected into a high-dimensional vector space (e.g. 1536 or 3072 dimensions) where semantically similar concepts cluster together.
3. **Self-Attention**: The transformer computes attention scores between all tokens to resolve context and relationships.
                        """.trimIndent(),
                        codeExample = """
import math

# Cosine similarity between two semantic vectors
def cosine_similarity(v1: list, v2: list) -> float:
    dot_product = sum(a * b for a, b in zip(v1, v2))
    mag1 = math.sqrt(sum(a * a for a in v1))
    mag2 = math.sqrt(sum(b * b for b in v2))
    return dot_product / (mag1 * mag2) if (mag1 * mag2) > 0 else 0.0

print("Similarity:", cosine_similarity([0.8, 0.2], [0.75, 0.25]))
                        """.trimIndent(),
                        practicePrompt = "Ask SUPER AI: 'Explain how the self-attention mechanism works using the sentence \"The animal didn't cross the street because it was too tired\".'",
                        quiz = QuizQuestion(
                            question = "Approximately how many English characters correspond to 1 standard LLM token?",
                            options = listOf("1 character", "4 characters", "25 characters", "100 characters"),
                            correctIndex = 1,
                            explanation = "In modern tokenizers, 1 token roughly corresponds to 4 characters or 0.75 words."
                        ),
                        miniProject = "Calculate the approximate token count for a 500-word blog post."
                    )
                )
            ),
            LearningLevel(
                levelNumber = 6,
                title = "Level 6 — AI App Development",
                subtitle = "APIs, Mobile Frontend, Backend & Production Architecture",
                iconName = "Devices",
                lessons = listOf(
                    Lesson(
                        id = "l6_fullstack_ai_architecture",
                        levelNumber = 6,
                        title = "Production Architecture for AI Apps",
                        shortSummary = "Decoupled frontend, secure backend gateway, and resilient error recovery.",
                        explanation = """
Building production-ready AI applications requires strict architectural hygiene:
- **Never expose API keys** in client-side code (use secure backend proxy or BuildConfig with secrets management).
- **Graceful degradation**: Implement fallback local synthesis when offline or rate-limited.
- **Streaming & Responsiveness**: Stream tokens or provide clear loading states to avoid perceived latency.
- **Entitlement Tiers**: Separate FREE vs PRO usage limits cleanly at the data layer.
                        """.trimIndent(),
                        codeExample = """
// Production Clean Architecture flow:
// UI Layer (Compose) -> ViewModel -> Repository -> AiEngine -> Retrofit / Cache
class AiViewModel(private val repository: ChatRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun askAi(prompt: String) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        repository.sendMessage(prompt)
    }
}
                        """.trimIndent(),
                        practicePrompt = "Ask SUPER AI: 'What are the best practices for caching AI responses to reduce API billing costs?'",
                        quiz = QuizQuestion(
                            question = "Why should AI API keys never be hardcoded into public client-side applications?",
                            options = listOf("They consume too much RAM", "Decompiled APKs expose keys to attackers for unauthorized billing", "Keys expire after 5 minutes", "Android does not support strings in code"),
                            correctIndex = 1,
                            explanation = "Client apps can be decompiled; sensitive keys must be protected via secure backend proxies or secret management."
                        ),
                        miniProject = "Map out the architecture diagram of SUPER AI connecting Jetpack Compose to the Gemini API."
                    )
                )
            )
        )
    }
}
