package com.example.data

import com.example.model.*

object DemoData {
    val employees = listOf(
        Employee(
            id = "emp_1",
            fullName = "Sarah Jenkins",
            email = "sarah.j@agency.com",
            phone = "+1 (555) 234-5678",
            role = UserRole.ADMIN,
            position = UserPosition.MANAGER,
            status = UserStatus.ACTIVE,
            hourlyRate = 85.0,
            avatarColor = 0xFF4F46E5
        ),
        Employee(
            id = "emp_2",
            fullName = "Alex Rivera",
            email = "alex.r@agency.com",
            phone = "+1 (555) 345-6789",
            role = UserRole.MANAGER,
            position = UserPosition.DEVELOPER,
            status = UserStatus.ACTIVE,
            hourlyRate = 65.0,
            avatarColor = 0xFF06B6D4
        ),
        Employee(
            id = "emp_3",
            fullName = "Elena Rostova",
            email = "elena.r@agency.com",
            phone = "+1 (555) 456-7890",
            role = UserRole.EMPLOYEE,
            position = UserPosition.DESIGNER,
            status = UserStatus.ACTIVE,
            hourlyRate = 50.0,
            avatarColor = 0xFFEC4899
        ),
        Employee(
            id = "emp_4",
            fullName = "Marcus Chen",
            email = "marcus.c@agency.com",
            phone = "+1 (555) 567-8901",
            role = UserRole.EMPLOYEE,
            position = UserPosition.VIDEO_EDITOR,
            status = UserStatus.ACTIVE,
            hourlyRate = 45.0,
            avatarColor = 0xFF8B5CF6
        ),
        Employee(
            id = "emp_5",
            fullName = "Amina Kabbaj",
            email = "amina.k@agency.com",
            phone = "+1 (555) 678-9012",
            role = UserRole.EMPLOYEE,
            position = UserPosition.MARKETING,
            status = UserStatus.AWAY,
            hourlyRate = 40.0,
            avatarColor = 0xFFF59E0B
        ),
        Employee(
            id = "emp_6",
            fullName = "David Miller",
            email = "david.m@agency.com",
            phone = "+1 (555) 789-0123",
            role = UserRole.EMPLOYEE,
            position = UserPosition.ACCOUNT_MANAGER,
            status = UserStatus.ACTIVE,
            hourlyRate = 48.0,
            avatarColor = 0xFF10B981
        )
    )

    val clients = listOf(
        Client(
            id = "cli_1",
            name = "Jonathan Vance",
            company = "Apex Fitness Co",
            phone = "+1 (555) 111-2233",
            email = "jonathan@apexfitness.io",
            whatsapp = "+15551112233",
            instagram = "@apexfitness",
            facebook = "apexfitnessco",
            address = "742 Evergreen Terr, Austin, TX",
            source = LeadSource.INSTAGRAM,
            status = ClientStatus.VIP,
            assignedEmployeeId = "emp_6",
            notes = "High-growth fitness brand launching 3 new supplement lines this quarter.",
            createdAt = System.currentTimeMillis() - 45 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 2 * 86400000L
        ),
        Client(
            id = "cli_2",
            name = "Sophia Dubois",
            company = "Luxe Parfums Paris",
            phone = "+33 6 12 34 56 78",
            email = "sophia@luxeparfums.fr",
            whatsapp = "+33612345678",
            instagram = "@luxeparfumsparis",
            facebook = "luxeparfums",
            address = "12 Rue de la Paix, Paris, FR",
            source = LeadSource.REFERRAL,
            status = ClientStatus.ACTIVE,
            assignedEmployeeId = "emp_1",
            notes = "Rebranding campaign and e-commerce revamp for holiday catalog.",
            createdAt = System.currentTimeMillis() - 30 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 1 * 86400000L
        ),
        Client(
            id = "cli_3",
            name = "Tariq Mansoor",
            company = "NeoPay Fintech",
            phone = "+971 50 123 4567",
            email = "tariq@neopay.ae",
            whatsapp = "+971501234567",
            instagram = "@neopay_app",
            facebook = "neopayfintech",
            address = "DIFC Gate Tower, Dubai, UAE",
            source = LeadSource.GOOGLE,
            status = ClientStatus.ACTIVE,
            assignedEmployeeId = "emp_2",
            notes = "Mobile app promotional videos and conversion landing page.",
            createdAt = System.currentTimeMillis() - 60 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 5 * 86400000L
        ),
        Client(
            id = "cli_4",
            name = "Chloe Simmons",
            company = "BrightWave Solar",
            phone = "+1 (555) 444-5566",
            email = "chloe@brightwavesolar.com",
            whatsapp = "+15554445566",
            instagram = "@brightwavesolar",
            facebook = "brightwavesolar",
            address = "500 Sun Blvd, Phoenix, AZ",
            source = LeadSource.WEBSITE,
            status = ClientStatus.ACTIVE,
            assignedEmployeeId = "emp_5",
            notes = "Lead generation ads on Meta & Google Ads.",
            createdAt = System.currentTimeMillis() - 25 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 3 * 86400000L
        ),
        Client(
            id = "cli_5",
            name = "Hans Weber",
            company = "Krono Watches",
            phone = "+49 30 9876543",
            email = "hans@kronowatches.de",
            whatsapp = "+49309876543",
            instagram = "@krono_timepieces",
            facebook = "kronowatches",
            address = "Friedrichstraße 40, Berlin, DE",
            source = LeadSource.TIKTOK,
            status = ClientStatus.PROSPECT,
            assignedEmployeeId = "emp_4",
            notes = "Interested in monthly UGC video package (15 clips/month).",
            createdAt = System.currentTimeMillis() - 10 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 16 * 86400000L
        ),
        Client(
            id = "cli_6",
            name = "Dr. Emily Zhao",
            company = "Aura Dental Care",
            phone = "+1 (555) 777-8899",
            email = "office@auradental.com",
            whatsapp = "+15557778899",
            instagram = "@auradental",
            facebook = "auradentalclinic",
            address = "1800 Beacon St, Boston, MA",
            source = LeadSource.FACEBOOK_ADS,
            status = ClientStatus.ACTIVE,
            assignedEmployeeId = "emp_6",
            notes = "Local SEO and social media content scheduling.",
            createdAt = System.currentTimeMillis() - 90 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 4 * 86400000L
        ),
        Client(
            id = "cli_7",
            name = "Gabriel Santos",
            company = "Verde Organics",
            phone = "+55 11 98765-4321",
            email = "gabriel@verdeorganics.br",
            whatsapp = "+5511987654321",
            instagram = "@verdeorganics",
            facebook = "verdeorganicos",
            address = "Av Paulista 1000, São Paulo, BR",
            source = LeadSource.EXISTING_CLIENT,
            status = ClientStatus.VIP,
            assignedEmployeeId = "emp_3",
            notes = "Eco-friendly packaging design and Shopify store revamp.",
            createdAt = System.currentTimeMillis() - 120 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 20 * 86400000L // overdue contact
        ),
        Client(
            id = "cli_8",
            name = "Liam O'Connor",
            company = "Dublin Roast Coffee",
            phone = "+353 1 234 5678",
            email = "liam@dublinroast.ie",
            whatsapp = "+35312345678",
            instagram = "@dublinroast",
            facebook = "dublinroastcoffee",
            address = "Grand Canal Quay, Dublin, IE",
            source = LeadSource.OTHER,
            status = ClientStatus.INACTIVE,
            assignedEmployeeId = "emp_5",
            notes = "Seasonal campaign ended; waiting for Q1 budget confirmation.",
            createdAt = System.currentTimeMillis() - 180 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 45 * 86400000L
        ),
        Client(
            id = "cli_9",
            name = "Maya Lin",
            company = "Zenith AI Robotics",
            phone = "+1 (555) 888-0011",
            email = "maya@zenithrobotics.tech",
            whatsapp = "+15558880011",
            instagram = "@zenithrobotics",
            facebook = "zenithrobotics",
            address = "Tech Hub 4, San Jose, CA",
            source = LeadSource.REFERRAL,
            status = ClientStatus.LEAD,
            assignedEmployeeId = "emp_1",
            notes = "Requested brand identity and 3D product rendering presentation.",
            createdAt = System.currentTimeMillis() - 4 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 1 * 86400000L
        ),
        Client(
            id = "cli_10",
            name = "Karim Benali",
            company = "Atlas Sahara Tours",
            phone = "+212 6 61 23 45 67",
            email = "karim@atlastours.ma",
            whatsapp = "+212661234567",
            instagram = "@atlassaharatours",
            facebook = "atlassaharatours",
            address = "Rue Moulay Youssef, Marrakech, MA",
            source = LeadSource.WHATSAPP,
            status = ClientStatus.ACTIVE,
            assignedEmployeeId = "emp_4",
            notes = "Cinematic video reel series for travel influencers and website booking engine.",
            createdAt = System.currentTimeMillis() - 35 * 86400000L,
            lastContactAt = System.currentTimeMillis() - 6 * 86400000L
        )
    )

    val projects = listOf(
        Project(
            projectId = "proj_1",
            name = "Apex Fitness Brand Refresh & E-Commerce",
            clientId = "cli_1",
            assignedEmployeeIds = listOf("emp_2", "emp_3"),
            type = ProjectType.WEBSITE,
            startDate = System.currentTimeMillis() - 14 * 86400000L,
            deadline = System.currentTimeMillis() + 3 * 86400000L, // 3 days left
            agreedDeliveryTime = "17:00",
            priority = Priority.URGENT,
            budget = 4800.0,
            paymentStatus = "Partially Paid",
            status = ProjectStatus.IN_PROGRESS,
            progress = 75,
            notes = "Completing checkout flow and Stripe payment gateway integration."
        ),
        Project(
            projectId = "proj_2",
            name = "Luxe Parfums Holiday Social Campaign",
            clientId = "cli_2",
            assignedEmployeeIds = listOf("emp_4", "emp_5"),
            type = ProjectType.SOCIAL_MEDIA,
            startDate = System.currentTimeMillis() - 20 * 86400000L,
            deadline = System.currentTimeMillis() + 86400000L, // Due tomorrow
            agreedDeliveryTime = "14:00",
            priority = Priority.HIGH,
            budget = 3200.0,
            paymentStatus = "Paid",
            status = ProjectStatus.REVIEW,
            progress = 90,
            notes = "12 curated Instagram carousels and 6 story sequences ready for client sign-off."
        ),
        Project(
            projectId = "proj_3",
            name = "NeoPay App Explainer 3D Video",
            clientId = "cli_3",
            assignedEmployeeIds = listOf("emp_4"),
            type = ProjectType.VIDEO_EDITING,
            startDate = System.currentTimeMillis() - 10 * 86400000L,
            deadline = System.currentTimeMillis() - 2 * 86400000L, // Overdue by 2 days!
            agreedDeliveryTime = "18:00",
            priority = Priority.HIGH,
            budget = 2500.0,
            paymentStatus = "Pending",
            status = ProjectStatus.DELAYED,
            progress = 65,
            notes = "Delayed waiting for voiceover re-recording from external talent."
        ),
        Project(
            projectId = "proj_4",
            name = "BrightWave Meta Lead Gen Funnel",
            clientId = "cli_4",
            assignedEmployeeIds = listOf("emp_5"),
            type = ProjectType.ADVERTISING,
            startDate = System.currentTimeMillis() - 5 * 86400000L,
            deadline = System.currentTimeMillis() + 10 * 86400000L,
            agreedDeliveryTime = "16:00",
            priority = Priority.MEDIUM,
            budget = 1900.0,
            paymentStatus = "Paid",
            status = ProjectStatus.IN_PROGRESS,
            progress = 40,
            notes = "Ad creatives in A/B testing on Meta Ad Manager."
        ),
        Project(
            projectId = "proj_5",
            name = "Aura Dental Smile Makeover UGC Ads",
            clientId = "cli_6",
            assignedEmployeeIds = listOf("emp_4", "emp_5"),
            type = ProjectType.UGC,
            startDate = System.currentTimeMillis() - 18 * 86400000L,
            deadline = System.currentTimeMillis() - 86400000L,
            agreedDeliveryTime = "12:00",
            priority = Priority.MEDIUM,
            budget = 1600.0,
            paymentStatus = "Paid",
            status = ProjectStatus.DELIVERED,
            progress = 100,
            deliveredAt = System.currentTimeMillis() - 86400000L,
            deliveredBy = "Marcus Chen",
            deliveryNotes = "High-definition Google Drive folder with 8 vertical ads delivered and confirmed.",
            notes = "Delivered to Dr. Zhao for approval."
        ),
        Project(
            projectId = "proj_6",
            name = "Verde Organics Biodegradable Box Design",
            clientId = "cli_7",
            assignedEmployeeIds = listOf("emp_3"),
            type = ProjectType.BRANDING,
            startDate = System.currentTimeMillis() - 30 * 86400000L,
            deadline = System.currentTimeMillis() - 5 * 86400000L, // overdue
            agreedDeliveryTime = "15:00",
            priority = Priority.HIGH,
            budget = 2800.0,
            paymentStatus = "Overdue",
            status = ProjectStatus.WAITING_CLIENT,
            progress = 85,
            notes = "Awaiting feedback on foil stamp print samples."
        ),
        Project(
            projectId = "proj_7",
            name = "Atlas Sahara Cinematic Travel Reel",
            clientId = "cli_10",
            assignedEmployeeIds = listOf("emp_4"),
            type = ProjectType.VIDEO_EDITING,
            startDate = System.currentTimeMillis() - 7 * 86400000L,
            deadline = System.currentTimeMillis() + 5 * 86400000L,
            agreedDeliveryTime = "19:00",
            priority = Priority.MEDIUM,
            budget = 2200.0,
            paymentStatus = "Partially Paid",
            status = ProjectStatus.IN_PROGRESS,
            progress = 50,
            notes = "Color grading drone 4K footage over Merzouga dunes."
        ),
        Project(
            projectId = "proj_8",
            name = "Zenith AI Robotics Identity Exploration",
            clientId = "cli_9",
            assignedEmployeeIds = listOf("emp_3", "emp_1"),
            type = ProjectType.BRANDING,
            startDate = System.currentTimeMillis() - 2 * 86400000L,
            deadline = System.currentTimeMillis() + 12 * 86400000L,
            agreedDeliveryTime = "17:00",
            priority = Priority.LOW,
            budget = 3500.0,
            paymentStatus = "Pending",
            status = ProjectStatus.NEW,
            progress = 15,
            notes = "Initial moodboard and typography pairings in progress."
        ),
        Project(
            projectId = "proj_9",
            name = "Krono Watches TikTok UGC Batch",
            clientId = "cli_5",
            assignedEmployeeIds = listOf("emp_4"),
            type = ProjectType.UGC,
            startDate = System.currentTimeMillis() - 4 * 86400000L,
            deadline = System.currentTimeMillis() + 6 * 86400000L,
            agreedDeliveryTime = "13:00",
            priority = Priority.MEDIUM,
            budget = 1400.0,
            paymentStatus = "Pending",
            status = ProjectStatus.IN_PROGRESS,
            progress = 30,
            notes = "Unboxing script approved by client."
        ),
        Project(
            projectId = "proj_10",
            name = "Apex Fitness Supplement Label Layouts",
            clientId = "cli_1",
            assignedEmployeeIds = listOf("emp_3"),
            type = ProjectType.GRAPHIC_DESIGN,
            startDate = System.currentTimeMillis() - 25 * 86400000L,
            deadline = System.currentTimeMillis() - 10 * 86400000L,
            agreedDeliveryTime = "17:00",
            priority = Priority.MEDIUM,
            budget = 1200.0,
            paymentStatus = "Paid",
            status = ProjectStatus.COMPLETED,
            progress = 100,
            deliveredAt = System.currentTimeMillis() - 10 * 86400000L,
            deliveredBy = "Elena Rostova",
            deliveryNotes = "Print-ready vector PDF files delivered.",
            notes = "Project completed with 5-star client review."
        ),
        Project(
            projectId = "proj_11",
            name = "NeoPay Web Portal Dashboard UI",
            clientId = "cli_3",
            assignedEmployeeIds = listOf("emp_2", "emp_3"),
            type = ProjectType.WEBSITE,
            startDate = System.currentTimeMillis() - 8 * 86400000L,
            deadline = System.currentTimeMillis() + 15 * 86400000L,
            agreedDeliveryTime = "18:00",
            priority = Priority.HIGH,
            budget = 5200.0,
            paymentStatus = "Pending",
            status = ProjectStatus.IN_PROGRESS,
            progress = 35,
            notes = "Figma design system handoff to React/Vue developers."
        ),
        Project(
            projectId = "proj_12",
            name = "Dublin Roast Q3 Packaging Design",
            clientId = "cli_8",
            assignedEmployeeIds = listOf("emp_3"),
            type = ProjectType.BRANDING,
            startDate = System.currentTimeMillis() - 120 * 86400000L,
            deadline = System.currentTimeMillis() - 90 * 86400000L,
            agreedDeliveryTime = "16:00",
            priority = Priority.LOW,
            budget = 1800.0,
            paymentStatus = "Paid",
            status = ProjectStatus.COMPLETED,
            progress = 100,
            deliveredAt = System.currentTimeMillis() - 90 * 86400000L,
            deliveredBy = "Elena Rostova",
            deliveryNotes = "All packaging assets finalized.",
            notes = "Archived."
        )
    )

    val tasks = listOf(
        Task(
            id = "tsk_1",
            title = "Finalize Apex Checkout integration",
            description = "Connect Stripe webhooks and test failed payment handling.",
            projectId = "proj_1",
            clientId = "cli_1",
            assignedEmployeeId = "emp_2",
            priority = Priority.URGENT,
            dueDate = System.currentTimeMillis() + 86400000L,
            status = TaskStatus.IN_PROGRESS
        ),
        Task(
            id = "tsk_2",
            title = "Review Luxe Parfums Carousel copy",
            description = "Proofread French and English captions for Instagram carousel.",
            projectId = "proj_2",
            clientId = "cli_2",
            assignedEmployeeId = "emp_5",
            priority = Priority.HIGH,
            dueDate = System.currentTimeMillis() + 4 * 3600000L,
            status = TaskStatus.REVIEW
        ),
        Task(
            id = "tsk_3",
            title = "Record voiceover for NeoPay 3D Reel",
            description = "Contact Arabic voiceover artist for revision.",
            projectId = "proj_3",
            clientId = "cli_3",
            assignedEmployeeId = "emp_4",
            priority = Priority.HIGH,
            dueDate = System.currentTimeMillis() - 86400000L, // Overdue
            status = TaskStatus.TODO
        ),
        Task(
            id = "tsk_4",
            title = "Set up Meta Pixel for BrightWave",
            description = "Verify custom events on thank-you page.",
            projectId = "proj_4",
            clientId = "cli_4",
            assignedEmployeeId = "emp_5",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() + 2 * 86400000L,
            status = TaskStatus.TODO
        ),
        Task(
            id = "tsk_5",
            title = "Export 4K masters for Atlas Sahara",
            description = "Render ProRes 422 and H.265 web versions.",
            projectId = "proj_7",
            clientId = "cli_10",
            assignedEmployeeId = "emp_4",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() + 3 * 86400000L,
            status = TaskStatus.IN_PROGRESS
        ),
        Task(
            id = "tsk_6",
            title = "Figma Wireframes for NeoPay Portal",
            description = "High fidelity transaction history & card management screen.",
            projectId = "proj_11",
            clientId = "cli_3",
            assignedEmployeeId = "emp_3",
            priority = Priority.HIGH,
            dueDate = System.currentTimeMillis() + 4 * 86400000L,
            status = TaskStatus.IN_PROGRESS
        ),
        Task(
            id = "tsk_7",
            title = "Moodboard for Zenith AI Robotics",
            description = "Curate cyberpunk minimalism visuals and futuristic typefaces.",
            projectId = "proj_8",
            clientId = "cli_9",
            assignedEmployeeId = "emp_3",
            priority = Priority.LOW,
            dueDate = System.currentTimeMillis() + 5 * 86400000L,
            status = TaskStatus.TODO
        ),
        Task(
            id = "tsk_8",
            title = "Deliver Aura Dental smile ads",
            description = "Upload 8 final MP4 files to client drive and send confirmation.",
            projectId = "proj_5",
            clientId = "cli_6",
            assignedEmployeeId = "emp_4",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() - 86400000L,
            status = TaskStatus.DONE,
            completedAt = System.currentTimeMillis() - 86400000L
        ),
        Task(
            id = "tsk_9",
            title = "Apex Mobile responsive audit",
            description = "Check iPhone 15 & Pixel 8 rendering on Safari/Chrome.",
            projectId = "proj_1",
            clientId = "cli_1",
            assignedEmployeeId = "emp_2",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() + 2 * 86400000L,
            status = TaskStatus.TODO
        ),
        Task(
            id = "tsk_10",
            title = "Krono Watches script breakdown",
            description = "Write 3 hook variants for TikTok creator.",
            projectId = "proj_9",
            clientId = "cli_5",
            assignedEmployeeId = "emp_4",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() + 86400000L,
            status = TaskStatus.IN_PROGRESS
        ),
        Task(
            id = "tsk_11",
            title = "Client feedback call with Jonathan (Apex)",
            description = "Demo live staging site and collect change requests.",
            projectId = "proj_1",
            clientId = "cli_1",
            assignedEmployeeId = "emp_1",
            priority = Priority.HIGH,
            dueDate = System.currentTimeMillis() + 3600000L * 3,
            status = TaskStatus.TODO
        ),
        Task(
            id = "tsk_12",
            title = "Color correction for Luxe Parfums video",
            description = "Match gold tones to official brand pantone.",
            projectId = "proj_2",
            clientId = "cli_2",
            assignedEmployeeId = "emp_4",
            priority = Priority.HIGH,
            dueDate = System.currentTimeMillis() - 3600000L,
            status = TaskStatus.DONE,
            completedAt = System.currentTimeMillis() - 2 * 3600000L
        ),
        Task(
            id = "tsk_13",
            title = "Verde Organics print spec verification",
            description = "Confirm bleed and CMYK profiles with packaging manufacturer.",
            projectId = "proj_6",
            clientId = "cli_7",
            assignedEmployeeId = "emp_3",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() + 6 * 86400000L,
            status = TaskStatus.REVIEW
        ),
        Task(
            id = "tsk_14",
            title = "Prepare Q4 Agency Revenue Report",
            description = "Calculate billable hours vs project margins.",
            projectId = "proj_1",
            clientId = "cli_1",
            assignedEmployeeId = "emp_1",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() + 7 * 86400000L,
            status = TaskStatus.TODO
        ),
        Task(
            id = "tsk_15",
            title = "Archive Dublin Roast assets",
            description = "Move raw camera footage to cold cloud storage.",
            projectId = "proj_12",
            clientId = "cli_8",
            assignedEmployeeId = "emp_4",
            priority = Priority.LOW,
            dueDate = System.currentTimeMillis() - 10 * 86400000L,
            status = TaskStatus.DONE,
            completedAt = System.currentTimeMillis() - 10 * 86400000L
        ),
        Task(
            id = "tsk_16",
            title = "Setup Google Analytics 4 for BrightWave",
            description = "Track conversion events and cost per acquisition.",
            projectId = "proj_4",
            clientId = "cli_4",
            assignedEmployeeId = "emp_5",
            priority = Priority.LOW,
            dueDate = System.currentTimeMillis() + 3 * 86400000L,
            status = TaskStatus.TODO
        ),
        Task(
            id = "tsk_17",
            title = "Send monthly retainer invoice to Atlas Tours",
            description = "50% upfront milestone for video reel production.",
            projectId = "proj_7",
            clientId = "cli_10",
            assignedEmployeeId = "emp_6",
            priority = Priority.HIGH,
            dueDate = System.currentTimeMillis() - 2 * 86400000L,
            status = TaskStatus.DONE,
            completedAt = System.currentTimeMillis() - 2 * 86400000L
        ),
        Task(
            id = "tsk_18",
            title = "Typography pairings for Zenith Robotics",
            description = "Compare Space Grotesk vs Plus Jakarta Sans.",
            projectId = "proj_8",
            clientId = "cli_9",
            assignedEmployeeId = "emp_3",
            priority = Priority.LOW,
            dueDate = System.currentTimeMillis() + 4 * 86400000L,
            status = TaskStatus.IN_PROGRESS
        ),
        Task(
            id = "tsk_19",
            title = "Instagram captions for BrightWave Solar",
            description = "Educational carousel about net metering savings.",
            projectId = "proj_4",
            clientId = "cli_4",
            assignedEmployeeId = "emp_5",
            priority = Priority.MEDIUM,
            dueDate = System.currentTimeMillis() + 86400000L,
            status = TaskStatus.REVIEW
        ),
        Task(
            id = "tsk_20",
            title = "Final code commit & deploy for Apex Fitness",
            description = "Push staging branch to production Vercel.",
            projectId = "proj_1",
            clientId = "cli_1",
            assignedEmployeeId = "emp_2",
            priority = Priority.URGENT,
            dueDate = System.currentTimeMillis() + 3 * 86400000L,
            status = TaskStatus.TODO
        )
    )

    val payments = listOf(
        Payment(
            id = "pay_1",
            clientId = "cli_1",
            projectId = "proj_1",
            amount = 2400.0,
            currency = "USD",
            paymentMethod = PaymentMethod.STRIPE,
            paymentDate = System.currentTimeMillis() - 14 * 86400000L,
            status = PaymentStatus.PAID,
            notes = "50% upfront deposit for e-commerce website."
        ),
        Payment(
            id = "pay_2",
            clientId = "cli_1",
            projectId = "proj_1",
            amount = 2400.0,
            currency = "USD",
            paymentMethod = PaymentMethod.STRIPE,
            paymentDate = System.currentTimeMillis() + 3 * 86400000L,
            status = PaymentStatus.PENDING,
            notes = "Final balance upon launch."
        ),
        Payment(
            id = "pay_3",
            clientId = "cli_2",
            projectId = "proj_2",
            amount = 3200.0,
            currency = "USD",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            paymentDate = System.currentTimeMillis() - 5 * 86400000L,
            status = PaymentStatus.PAID,
            notes = "Full payment for Holiday Social Campaign."
        ),
        Payment(
            id = "pay_4",
            clientId = "cli_3",
            projectId = "proj_3",
            amount = 2500.0,
            currency = "USD",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            paymentDate = System.currentTimeMillis() - 3 * 86400000L,
            status = PaymentStatus.OVERDUE,
            notes = "Invoice #INV-2024-038 overdue by 3 days."
        ),
        Payment(
            id = "pay_5",
            clientId = "cli_4",
            projectId = "proj_4",
            amount = 1900.0,
            currency = "USD",
            paymentMethod = PaymentMethod.STRIPE,
            paymentDate = System.currentTimeMillis() - 4 * 86400000L,
            status = PaymentStatus.PAID,
            notes = "Monthly Meta Ads campaign retainer."
        ),
        Payment(
            id = "pay_6",
            clientId = "cli_6",
            projectId = "proj_5",
            amount = 1600.0,
            currency = "USD",
            paymentMethod = PaymentMethod.PAYPAL,
            paymentDate = System.currentTimeMillis() - 2 * 86400000L,
            status = PaymentStatus.PAID,
            notes = "UGC Video production fee."
        ),
        Payment(
            id = "pay_7",
            clientId = "cli_7",
            projectId = "proj_6",
            amount = 2800.0,
            currency = "USD",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            paymentDate = System.currentTimeMillis() - 7 * 86400000L,
            status = PaymentStatus.OVERDUE,
            notes = "Packaging design milestone payment delayed."
        ),
        Payment(
            id = "pay_8",
            clientId = "cli_10",
            projectId = "proj_7",
            amount = 1100.0,
            currency = "USD",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            paymentDate = System.currentTimeMillis() - 6 * 86400000L,
            status = PaymentStatus.PAID,
            notes = "First installment for Sahara travel reel."
        ),
        Payment(
            id = "pay_9",
            clientId = "cli_10",
            projectId = "proj_7",
            amount = 1100.0,
            currency = "USD",
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            paymentDate = System.currentTimeMillis() + 5 * 86400000L,
            status = PaymentStatus.PENDING,
            notes = "Final reel delivery balance."
        ),
        Payment(
            id = "pay_10",
            clientId = "cli_1",
            projectId = "proj_10",
            amount = 1200.0,
            currency = "USD",
            paymentMethod = PaymentMethod.CASH,
            paymentDate = System.currentTimeMillis() - 15 * 86400000L,
            status = PaymentStatus.PAID,
            notes = "Supplement label designs (complete)."
        )
    )

    val calendarEvents = listOf(
        CalendarEvent(
            id = "ev_1",
            title = "Apex Fitness Live Demo & Review",
            type = EventType.CLIENT_CALL,
            startMillis = System.currentTimeMillis() + 3 * 3600000L,
            endMillis = System.currentTimeMillis() + 4 * 3600000L,
            linkedClientId = "cli_1",
            linkedProjectId = "proj_1",
            assignedUserId = "emp_1",
            notes = "Go through checkout flow and product catalog on staging."
        ),
        CalendarEvent(
            id = "ev_2",
            title = "Luxe Parfums Delivery Deadline",
            type = EventType.PROJECT_DEADLINE,
            startMillis = System.currentTimeMillis() + 86400000L,
            endMillis = System.currentTimeMillis() + 86400000L + 1800000L,
            linkedClientId = "cli_2",
            linkedProjectId = "proj_2",
            assignedUserId = "emp_4",
            notes = "Final delivery of 12 Instagram carousels."
        ),
        CalendarEvent(
            id = "ev_3",
            title = "Weekly Creative Team Standup",
            type = EventType.MEETING,
            startMillis = System.currentTimeMillis() + 86400000L + 3600000L * 2,
            endMillis = System.currentTimeMillis() + 86400000L + 3600000L * 3,
            assignedUserId = "emp_1",
            notes = "Review workload, deadlines, and client health scores."
        ),
        CalendarEvent(
            id = "ev_4",
            title = "NeoPay Urgent Status Sync",
            type = EventType.CLIENT_CALL,
            startMillis = System.currentTimeMillis() + 2 * 86400000L,
            endMillis = System.currentTimeMillis() + 2 * 86400000L + 1800000L,
            linkedClientId = "cli_3",
            linkedProjectId = "proj_3",
            assignedUserId = "emp_2",
            notes = "Discuss voiceover artist revision and revised delivery date."
        ),
        CalendarEvent(
            id = "ev_5",
            title = "Atlas Sahara Final 4K Delivery",
            type = EventType.DELIVERY,
            startMillis = System.currentTimeMillis() + 5 * 86400000L,
            endMillis = System.currentTimeMillis() + 5 * 86400000L + 3600000L,
            linkedClientId = "cli_10",
            linkedProjectId = "proj_7",
            assignedUserId = "emp_4",
            notes = "Handover drone video reels."
        )
    )

    val reminders = listOf(
        Reminder(
            id = "rem_1",
            title = "Follow up with Tariq (NeoPay) about overdue invoice",
            description = "Send reminder email with updated bank transfer wire details.",
            dateMillis = System.currentTimeMillis(),
            timeString = "14:00",
            userId = "emp_1",
            relatedClientId = "cli_3",
            relatedProjectId = "proj_3",
            completed = false
        ),
        Reminder(
            id = "rem_2",
            title = "Check Verde Organics packaging samples",
            description = "Call printing house to check if sample package arrived.",
            dateMillis = System.currentTimeMillis() + 86400000L,
            timeString = "11:00",
            userId = "emp_3",
            relatedClientId = "cli_7",
            relatedProjectId = "proj_6",
            completed = false
        ),
        Reminder(
            id = "rem_3",
            title = "Send monthly agency metric report to partners",
            description = "Export CSV and revenue summaries.",
            dateMillis = System.currentTimeMillis() + 3 * 86400000L,
            timeString = "16:30",
            userId = "emp_1",
            completed = false
        )
    )

    val notifications = listOf(
        NotificationItem(
            id = "notif_1",
            userId = "emp_1",
            type = "alert",
            title = "Project Delayed: NeoPay Explainer",
            message = "NeoPay 3D Video is overdue by 2 days. Requires attention.",
            relatedId = "proj_3",
            read = false,
            createdAt = System.currentTimeMillis() - 2 * 3600000L
        ),
        NotificationItem(
            id = "notif_2",
            userId = "emp_1",
            type = "payment",
            title = "Invoice Overdue: $2,500.00",
            message = "NeoPay Fintech payment is past due date.",
            relatedId = "pay_4",
            read = false,
            createdAt = System.currentTimeMillis() - 5 * 3600000L
        ),
        NotificationItem(
            id = "notif_3",
            userId = "emp_1",
            type = "deadline",
            title = "Deadline Tomorrow: Luxe Parfums",
            message = "Holiday social campaign delivery due tomorrow at 14:00.",
            relatedId = "proj_2",
            read = false,
            createdAt = System.currentTimeMillis() - 8 * 3600000L
        ),
        NotificationItem(
            id = "notif_4",
            userId = "emp_1",
            type = "delivery",
            title = "Project Delivered: Aura Dental",
            message = "Marcus Chen marked Smile Makeover UGC Ads as Delivered.",
            relatedId = "proj_5",
            read = true,
            createdAt = System.currentTimeMillis() - 24 * 3600000L
        ),
        NotificationItem(
            id = "notif_5",
            userId = "emp_1",
            type = "client",
            title = "New VIP Client Added: Jonathan Vance",
            message = "Apex Fitness Co has been registered with $4,800 project.",
            relatedId = "cli_1",
            read = true,
            createdAt = System.currentTimeMillis() - 48 * 3600000L
        )
    )

    val communicationLogs = listOf(
        CommunicationLog(
            id = "com_1",
            clientId = "cli_1",
            dateMillis = System.currentTimeMillis() - 2 * 86400000L,
            type = CommunicationType.MEETING,
            employeeId = "emp_1",
            note = "Met on Google Meet to review website prototypes. Jonathan approved color palette and navigation."
        ),
        CommunicationLog(
            id = "com_2",
            clientId = "cli_1",
            dateMillis = System.currentTimeMillis() - 7 * 86400000L,
            type = CommunicationType.WHATSAPP,
            employeeId = "emp_6",
            note = "Sent updated product photography guidelines and catalog spreadsheet."
        ),
        CommunicationLog(
            id = "com_3",
            clientId = "cli_2",
            dateMillis = System.currentTimeMillis() - 1 * 86400000L,
            type = CommunicationType.EMAIL,
            employeeId = "emp_1",
            note = "Sent preview link for holiday social media carousels. Client loved the gold aesthetics."
        ),
        CommunicationLog(
            id = "com_4",
            clientId = "cli_3",
            dateMillis = System.currentTimeMillis() - 5 * 86400000L,
            type = CommunicationType.PHONE,
            employeeId = "emp_2",
            note = "Discussed audio delay and voiceover pacing. Tariq promised to wire invoice by Thursday."
        ),
        CommunicationLog(
            id = "com_5",
            clientId = "cli_4",
            dateMillis = System.currentTimeMillis() - 3 * 86400000L,
            type = CommunicationType.WHATSAPP,
            employeeId = "emp_5",
            note = "Shared weekly Meta Ads ROAS report: Cost per lead decreased by 22%."
        )
    )

    val activityLogs = listOf(
        ActivityLog(
            id = "act_1",
            actorUserId = "emp_1",
            actorName = "Sarah Jenkins",
            action = "UPDATED",
            entityType = "Project",
            entityId = "proj_1",
            description = "Updated progress to 75% on Apex Fitness Brand Refresh",
            createdAt = System.currentTimeMillis() - 30 * 60000L
        ),
        ActivityLog(
            id = "act_2",
            actorUserId = "emp_4",
            actorName = "Marcus Chen",
            action = "DELIVERED",
            entityType = "Project",
            entityId = "proj_5",
            description = "Marked Aura Dental UGC Ads as Delivered",
            createdAt = System.currentTimeMillis() - 4 * 3600000L
        ),
        ActivityLog(
            id = "act_3",
            actorUserId = "emp_2",
            actorName = "Alex Rivera",
            action = "MOVED_TASK",
            entityType = "Task",
            entityId = "tsk_1",
            description = "Moved 'Finalize Apex Checkout integration' to IN PROGRESS",
            createdAt = System.currentTimeMillis() - 8 * 3600000L
        ),
        ActivityLog(
            id = "act_4",
            actorUserId = "emp_6",
            actorName = "David Miller",
            action = "PAYMENT_RECORDED",
            entityType = "Payment",
            entityId = "pay_6",
            description = "Recorded payment of $1,600.00 from Aura Dental Care",
            createdAt = System.currentTimeMillis() - 24 * 3600000L
        ),
        ActivityLog(
            id = "act_5",
            actorUserId = "emp_1",
            actorName = "Sarah Jenkins",
            action = "CREATED_CLIENT",
            entityType = "Client",
            entityId = "cli_9",
            description = "Created new client profile: Zenith AI Robotics",
            createdAt = System.currentTimeMillis() - 48 * 3600000L
        )
    )

    val projectFiles = listOf(
        ProjectFile(
            id = "fil_1",
            fileName = "Apex_Brand_Guidelines_v2.pdf",
            storagePath = "projects/proj_1/brief/Apex_Brand_Guidelines_v2.pdf",
            downloadURL = "https://firebasestorage.googleapis.com/v0/b/agency/o/brief.pdf",
            uploadedBy = "Sarah Jenkins",
            uploadedAt = System.currentTimeMillis() - 10 * 86400000L,
            fileSize = 3450000L,
            category = FileCategory.BRIEF,
            projectId = "proj_1",
            clientId = "cli_1"
        ),
        ProjectFile(
            id = "fil_2",
            fileName = "Checkout_Figma_Export.png",
            storagePath = "projects/proj_1/images/Checkout_Figma_Export.png",
            downloadURL = "https://firebasestorage.googleapis.com/v0/b/agency/o/checkout.png",
            uploadedBy = "Alex Rivera",
            uploadedAt = System.currentTimeMillis() - 2 * 86400000L,
            fileSize = 1820000L,
            category = FileCategory.IMAGES,
            projectId = "proj_1",
            clientId = "cli_1"
        ),
        ProjectFile(
            id = "fil_3",
            fileName = "Luxe_Holiday_Carousels_Master.zip",
            storagePath = "projects/proj_2/final/Luxe_Holiday_Carousels_Master.zip",
            downloadURL = "https://firebasestorage.googleapis.com/v0/b/agency/o/luxe_final.zip",
            uploadedBy = "Elena Rostova",
            uploadedAt = System.currentTimeMillis() - 12 * 3600000L,
            fileSize = 14200000L,
            category = FileCategory.FINAL_FILES,
            projectId = "proj_2",
            clientId = "cli_2"
        ),
        ProjectFile(
            id = "fil_4",
            fileName = "NeoPay_Explainer_Storyboard.pdf",
            storagePath = "projects/proj_3/documents/NeoPay_Explainer_Storyboard.pdf",
            downloadURL = "https://firebasestorage.googleapis.com/v0/b/agency/o/storyboard.pdf",
            uploadedBy = "Marcus Chen",
            uploadedAt = System.currentTimeMillis() - 8 * 86400000L,
            fileSize = 4200000L,
            category = FileCategory.DOCUMENTS,
            projectId = "proj_3",
            clientId = "cli_3"
        )
    )
}
