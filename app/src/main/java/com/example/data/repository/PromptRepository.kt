package com.example.data.repository

data class PromptCategory(
    val id: String,
    val name: String,
    val iconName: String,
    val placeholder: String,
    val sampleGoal: String,
    val parameters: List<String>
)

data class GeneratedPromptResult(
    val categoryId: String,
    val goal: String,
    val masterPrompt: String,
    val negativePrompt: String? = null,
    val suggestedModel: String
)

class PromptRepository {
    fun getCategories(): List<PromptCategory> {
        return listOf(
            PromptCategory(
                id = "image",
                name = "AI Image Prompt",
                iconName = "Image",
                placeholder = "e.g. Cyberpunk street market with glowing neon signs in rain",
                sampleGoal = "Futuristic isometric smart city with clean solar glass skyscrapers and flying electric shuttles at golden hour",
                parameters = listOf("Art Style: Photorealistic / 3D Render / Anime", "Lighting: Volumetric / Golden Hour / Cyberpunk Neon", "Aspect Ratio: 16:9 / 1:1 / 9:16")
            ),
            PromptCategory(
                id = "video",
                name = "AI Video Prompt",
                iconName = "Videocam",
                placeholder = "e.g. Cinematic camera pan across mountain peaks",
                sampleGoal = "Slow cinematic aerial dolly zoom over misty Scandinavian pine forest at dawn, 4K 60fps slow motion",
                parameters = listOf("Camera Movement: Dolly Zoom / FPV Drone / Orbit", "Pacing: Slow Cinematic / Dynamic Action")
            ),
            PromptCategory(
                id = "coding",
                name = "Coding Prompt",
                iconName = "Code",
                placeholder = "e.g. Build an authenticated REST API in Python FastAPI with PostgreSQL",
                sampleGoal = "Create a scalable token-bucket rate limiter middleware in Go with Redis backend and Prometheus metrics",
                parameters = listOf("Language: Python / TypeScript / Kotlin / Go", "Architecture: Clean / Microservice / Modular")
            ),
            PromptCategory(
                id = "business",
                name = "Business Prompt",
                iconName = "Business",
                placeholder = "e.g. Validate B2B SaaS for automated inventory forecasting",
                sampleGoal = "Formulate a comprehensive competitive moat analysis and go-to-market plan for an AI study app",
                parameters = listOf("Focus: GTM / Unit Economics / Pitch Deck", "Tone: Venture Capital / Pragmatic Operator")
            ),
            PromptCategory(
                id = "study",
                name = "Study Prompt",
                iconName = "School",
                placeholder = "e.g. Master quantum entanglement for college physics exam",
                sampleGoal = "Teach me Dynamic Programming using the Feynman technique, starting with the Fibonacci memoization tree",
                parameters = listOf("Learning Style: Feynman Analogy / Socratic Dialogue", "Difficulty: High School / Undergraduate / PhD")
            ),
            PromptCategory(
                id = "writing",
                name = "Writing Prompt",
                iconName = "Edit",
                placeholder = "e.g. Thought leadership essay on human-AI collaboration",
                sampleGoal = "Draft a compelling 800-word newsletter essay on how junior engineers can thrive in the age of AI coding agents",
                parameters = listOf("Format: Newsletter / Essay / Blog", "Tone: Persuasive / Thoughtful / Authoritative")
            ),
            PromptCategory(
                id = "research",
                name = "Research Prompt",
                iconName = "Science",
                placeholder = "e.g. Literature review on transformer attention efficiency",
                sampleGoal = "Synthesize recent 2024-2026 breakthroughs in FlashAttention, sparse mixture-of-experts, and state space models",
                parameters = listOf("Depth: Executive Summary / Academic Literature Review")
            ),
            PromptCategory(
                id = "content",
                name = "Content Creation Prompt",
                iconName = "Campaign",
                placeholder = "e.g. Viral tech breakdown video script for YouTube",
                sampleGoal = "Create a high-retention 60-second TikTok script explaining why quantum computers won't replace your phone",
                parameters = listOf("Platform: YouTube / TikTok / X Thread", "Goal: Virality / Retention / Educational")
            )
        )
    }

    fun buildEngineeredPrompt(category: String, userGoal: String, styleOrTone: String): GeneratedPromptResult {
        val master = when (category) {
            "image" -> """
Photo-masterpiece: $userGoal.
Aesthetic Style: $styleOrTone, hyper-detailed textures, ray-traced ambient occlusion, 8K resolution, octane render lighting, cinematic composition, award-winning visual depth.
Negative constraints: blurry, low resolution, watermark, deformed anatomy, noisy artifacts.
            """.trimIndent()

            "video" -> """
Cinematic 4K Shot: $userGoal.
Motion Dynamics: $styleOrTone, fluid camera tracking, high dynamic range, photorealistic physics, 60fps temporal coherence.
            """.trimIndent()

            "coding" -> """
[ROLE]: Principal Staff Software Architect.
[OBJECTIVE]: $userGoal
[REQUIREMENTS]:
1. Implement in $styleOrTone with strict idiomatic best practices.
2. Include comprehensive type signatures, docstrings, and error handling for edge cases.
3. Optimize algorithmic complexity (time and space).
4. Provide unit test cases covering edge scenarios.
[OUTPUT]: Production-ready code block followed by concise explanation.
            """.trimIndent()

            "study" -> """
[ROLE]: Master University Professor & Learning Coach.
[TOPIC]: $userGoal
[TEACHING METHOD]: $styleOrTone
[STRUCTURE]:
1. Core Intuition in 2 sentences.
2. Step-by-Step Mechanism breakdown with diagrams/code where applicable.
3. Common misconceptions to avoid.
4. Active Recall: 3 targeted quiz questions with explanations.
            """.trimIndent()

            "business" -> """
[ROLE]: Veteran Startup Operator and Venture Partner.
[SUBJECT]: $userGoal
[DELIVERABLE]:
1. Executive Value Proposition & Core Hypothesis.
2. Ideal Customer Profile (ICP) & Pain Points.
3. Go-to-Market (GTM) Strategy: $styleOrTone.
4. Risk Matrix & 30-Day Validation Milestones.
            """.trimIndent()

            else -> """
[ROLE]: Expert AI Specialist.
[TASK]: $userGoal
[TONE & CONSTRAINTS]: $styleOrTone, precise, structured, actionable, and free of fluff.
            """.trimIndent()
        }

        return GeneratedPromptResult(
            categoryId = category,
            goal = userGoal,
            masterPrompt = master,
            suggestedModel = if (category == "image" || category == "video") "Gemini Flash Image / Veo" else "gemini-3.5-flash"
        )
    }
}
