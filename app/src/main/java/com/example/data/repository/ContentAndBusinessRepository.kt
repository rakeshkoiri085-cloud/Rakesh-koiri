package com.example.data.repository

data class ContentTemplate(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val defaultPrompt: String
)

data class BusinessTool(
    val id: String,
    val title: String,
    val description: String,
    val promptTemplate: String
)

class ContentAndBusinessRepository {
    fun getContentTemplates(): List<ContentTemplate> {
        return listOf(
            ContentTemplate(
                id = "yt_script",
                title = "YouTube Video Script",
                category = "Video",
                description = "Full structured script with hook, body points, visual cues, and call-to-action.",
                defaultPrompt = "Write an engaging 8-minute YouTube video script about: "
            ),
            ContentTemplate(
                id = "viral_hooks",
                title = "10 Viral Video Hooks",
                category = "Shorts & Reels",
                description = "High-retention opening lines to boost your 3-second watch percentage.",
                defaultPrompt = "Generate 10 irresistible video hooks for: "
            ),
            ContentTemplate(
                id = "ig_captions",
                title = "Instagram Captions & Hashtags",
                category = "Social Media",
                description = "Engaging caption with micro-storytelling, emojis, and 15 targeted hashtags.",
                defaultPrompt = "Create a compelling Instagram carousel caption for: "
            ),
            ContentTemplate(
                id = "content_calendar",
                title = "30-Day Content Calendar",
                category = "Planning",
                description = "Organized weekly schedule with content pillars, formats, and titles.",
                defaultPrompt = "Design a 30-day content calendar for a creator in the niche: "
            ),
            ContentTemplate(
                id = "shorts_ideas",
                title = "Short-Form Video Ideas",
                category = "Shorts & Reels",
                description = "5 high-converting video concepts designed for TikTok and YouTube Shorts.",
                defaultPrompt = "Provide 5 fresh, high-retention short video concepts for: "
            )
        )
    }

    fun getBusinessTools(): List<BusinessTool> {
        return listOf(
            BusinessTool(
                id = "idea_generator",
                title = "AI Business Idea Generator",
                description = "Generate profitable, low-capital startup ideas solving real problems.",
                promptTemplate = "Generate 3 innovative, low-overhead business ideas in the domain of: "
            ),
            BusinessTool(
                id = "lean_plan",
                title = "1-Page Lean Business Plan",
                description = "Problem, solution, unique value proposition, revenue model, and key metrics.",
                promptTemplate = "Draft a comprehensive 1-page lean business plan for this concept: "
            ),
            BusinessTool(
                id = "target_icp",
                title = "Ideal Customer Profile (ICP)",
                description = "Demographics, psychological pain points, habits, and buying triggers.",
                promptTemplate = "Identify the detailed Ideal Customer Profile (ICP) and pain points for: "
            ),
            BusinessTool(
                id = "risk_analysis",
                title = "Risk & Mitigation Analysis",
                description = "Identify operational, market, and financial risks with proactive solutions.",
                promptTemplate = "Perform a thorough risk and mitigation matrix for: "
            ),
            BusinessTool(
                id = "marketing_gtm",
                title = "Go-To-Market (GTM) Plan",
                description = "Organic distribution channels, influencer outreach, and launch strategy.",
                promptTemplate = "Formulate a zero-to-one organic marketing launch plan for: "
            )
        )
    }
}
