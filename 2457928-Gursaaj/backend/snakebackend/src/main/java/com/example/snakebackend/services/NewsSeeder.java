package com.example.snakebackend.services;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.snakebackend.models.NewsPost;

/**
 * Populates the News & Facts feed with a curated set of SDG 14 posts
 * the first time the app starts. Runs only when the table is empty,
 * so restarts don't duplicate rows.
 *
 * Keeping this in the services package (instead of a
 * @PostConstruct on the service itself) lets Spring wire a real
 * {@link NewsService} and means the seeder is easy to disable by
 * just removing the bean.
 */
@Component
public class NewsSeeder implements CommandLineRunner {

    private final NewsService news;

    public NewsSeeder(NewsService news) {
        this.news = news;
    }

    @Override
    public void run(String... args) {
        if (news.count() > 0) return;  // already seeded — skip

        LocalDateTime now = LocalDateTime.now();

        news.savePost(new NewsPost(
            "8 million tonnes of plastic enter the ocean every year",
            "Roughly a garbage truck's worth of plastic is dumped into the sea every single minute, according to the UN Environment Programme.",
            "Plastic pollution is the most visible symptom of ocean pollution, but its scale is hard to grasp. " +
                "The UN Environment Programme estimates that around 8 million tonnes of plastic waste enter the ocean each year — " +
                "the equivalent of dumping a full garbage truck every single minute.\n\n" +
                "Most of this plastic never breaks down; it just fragments into smaller and smaller pieces called microplastics, " +
                "which end up inside fish, sea birds, and ultimately on our plates. Around 80% of ocean plastic originates from land-based sources " +
                "— rivers, drains, landfill runoff and litter from coastal cities.\n\n" +
                "The good news: since most ocean plastic comes from land, most of it is preventable. Reducing single-use packaging, " +
                "improving waste collection in coastal regions, and supporting extended producer responsibility laws are the three interventions " +
                "researchers identify as having the biggest impact.",
            "plastic", "\uD83C\uDF0A",
            "UN Environment Programme",
            "https://www.unep.org/interactives/beat-plastic-pollution/",
            now.minusDays(1)
        ));

        news.savePost(new NewsPost(
            "Over 800 marine species affected by ocean debris",
            "From sea turtles mistaking plastic bags for jellyfish to whales dying with stomachs full of waste, debris now affects more than 800 species.",
            "A UNESCO-IOC review found that over 800 species of marine life are now affected by ocean debris — up from fewer than 250 species in 1997. " +
                "Entanglement and ingestion are the two most common harms.\n\n" +
                "Sea turtles are especially vulnerable: a floating plastic bag is almost indistinguishable from a jellyfish, their primary food. " +
                "Seabirds like the Laysan albatross feed plastic fragments to their chicks, mistaking them for squid. Whales have washed ashore " +
                "with tens of kilograms of plastic bags and fishing gear inside their stomachs.\n\n" +
                "Ghost fishing — abandoned nets that keep catching and killing marine life long after they're lost — accounts for " +
                "an estimated 10% of all ocean plastic by weight, despite being a small fraction of items.",
            "species", "\uD83D\uDC22",
            "UNESCO-IOC",
            "https://www.unesco.org/en/ocean",
            now.minusDays(2)
        ));

        news.savePost(new NewsPost(
            "Coral reefs have lost half their cover since 1950",
            "Rising temperatures, acidification and pollution have wiped out roughly 50% of global coral cover in the last 70 years.",
            "Coral reefs support around 25% of all marine species despite covering less than 1% of the ocean floor. A landmark 2021 study in " +
                "One Earth found that global coral cover has roughly halved since the 1950s, driven by a combination of ocean warming, acidification, " +
                "overfishing, and land-based pollution.\n\n" +
                "Warming events cause mass bleaching: corals expel the symbiotic algae living in their tissues and turn bone-white. If the heat " +
                "lasts too long, the coral dies. The 2016 Great Barrier Reef event alone killed roughly 30% of that reef's corals in a single summer.\n\n" +
                "Reef restoration projects — coral nurseries, larval seeding, and outplanting heat-tolerant strains — are now underway in more than " +
                "50 countries, though scientists stress that the only long-term fix is limiting warming below 1.5 °C.",
            "science", "\uD83E\uDEB8",
            "One Earth / IUCN",
            "https://www.iucn.org/resources/issues-brief/coral-reefs-and-climate-change",
            now.minusDays(3)
        ));

        news.savePost(new NewsPost(
            "Global treaty on plastic pollution under negotiation",
            "193 countries are negotiating the first-ever legally binding UN treaty to end plastic pollution, with a target deadline this decade.",
            "In March 2022, the UN Environment Assembly voted to develop the first legally binding global treaty on plastic pollution. " +
                "Talks — led by the Intergovernmental Negotiating Committee (INC) — aim to cover the full life cycle of plastic, from fossil-fuel " +
                "feedstocks to product design to waste management.\n\n" +
                "Advocates argue the treaty is the biggest environmental deal since the Paris Agreement. Points of contention include whether to cap " +
                "virgin plastic production, whether to phase out specific hazardous additives, and how to fund waste-collection systems in lower-income " +
                "countries that receive much of the world's plastic waste exports.\n\n" +
                "A finalized treaty text is expected this decade. If successful, it would be the first instrument to treat plastic as a global commons problem " +
                "rather than a local litter issue.",
            "progress", "\uD83D\uDCDC",
            "UN Environment Assembly",
            "https://www.unep.org/inc-plastic-pollution",
            now.minusDays(5)
        ));

        news.savePost(new NewsPost(
            "Marine Protected Areas now cover 8% of the ocean",
            "Countries are on track to protect 30% of oceans by 2030 under the \"30x30\" target, with 8% already protected today.",
            "Marine Protected Areas (MPAs) are regions of the sea where human activity is restricted to conserve biodiversity. " +
                "As of 2024, around 8% of the global ocean is under some form of protection — a big jump from under 1% in 2000, though still " +
                "far short of the 30% target set at the 2022 UN biodiversity summit (\"30x30\").\n\n" +
                "Not all MPAs are equal: many allow commercial fishing inside their boundaries, which scientists call \"paper parks\". " +
                "Fully-protected no-take zones make up only about 2.9% of the ocean. Where strict MPAs exist, fish populations inside their " +
                "borders are on average 670% larger than outside, and biodiversity is notably higher.\n\n" +
                "The challenge now is enforcement: satellites, AIS vessel tracking, and AI-powered monitoring tools are becoming the " +
                "front line of marine conservation.",
            "progress", "\uD83D\uDEE1\uFE0F",
            "Marine Conservation Institute",
            "https://mpatlas.org/",
            now.minusDays(6)
        ));

        news.savePost(new NewsPost(
            "Ocean is absorbing 25% of humanity's CO\u2082 emissions",
            "The sea has absorbed roughly a quarter of every ton of CO\u2082 we've ever emitted — and is becoming measurably more acidic.",
            "The ocean is the planet's largest active carbon sink. Since the industrial revolution, it has absorbed roughly 25-30% of all CO\u2082 " +
                "emissions from human activity. That sounds like good news — and in terms of slowing global warming, it is — but it comes at a cost.\n\n" +
                "When CO\u2082 dissolves in seawater it forms carbonic acid. Average ocean surface pH has dropped by about 0.1 units since 1850, " +
                "which corresponds to a 30% increase in acidity. This is bad for anything that builds a shell or skeleton out of calcium carbonate: " +
                "corals, oysters, mussels, pteropods, and many planktonic species.\n\n" +
                "Scientists call this \"the other CO\u2082 problem\". Unlike warming, there's no local fix — acidification is directly tied to how " +
                "much CO\u2082 we put into the atmosphere globally.",
            "science", "\uD83D\uDCA8",
            "NOAA Pacific Marine Environmental Laboratory",
            "https://www.pmel.noaa.gov/co2/",
            now.minusDays(8)
        ));

        news.savePost(new NewsPost(
            "One reusable bottle replaces 167 plastic ones a year",
            "Switching to a refillable water bottle prevents about 167 single-use plastic bottles from being produced per person, per year.",
            "Small habit changes add up. The Earth Day Network estimates that the average American uses 167 single-use plastic water bottles a year, " +
                "and only about 38 of them are recycled. Switching to a single refillable bottle — stainless steel, glass, or BPA-free plastic — " +
                "eliminates almost all of that waste stream.\n\n" +
                "Other high-impact swaps: refusing plastic straws (500 million are used per day in the US alone), choosing bar soap and shampoo " +
                "over bottled, carrying a reusable shopping bag, and buying in bulk to cut packaging.\n\n" +
                "None of this replaces structural change — 70% of plastic pollution comes from industrial sources and packaging — but personal action " +
                "changes what feels normal, and that shifts consumer demand fast.",
            "action", "\uD83D\uDCA7",
            "Earth Day Network",
            "https://www.earthday.org/",
            now.minusDays(10)
        ));

        news.savePost(new NewsPost(
            "The Great Pacific Garbage Patch is twice the size of Texas",
            "A swirling vortex of plastic between Hawaii and California now covers 1.6 million km\u00B2 and holds an estimated 1.8 trillion pieces of debris.",
            "The Great Pacific Garbage Patch isn't a solid island you can walk across — it's a diffuse cloud of plastic trapped in an ocean gyre, " +
                "most of it below the surface. A 2018 study in Scientific Reports mapped it at roughly 1.6 million square kilometres: twice the size of Texas, " +
                "or three times the size of France.\n\n" +
                "The study estimated 1.8 trillion plastic pieces floating there, weighing around 80,000 tonnes. Most of it is old, weathered fishing gear — " +
                "nets, ropes, and crates — which is why many researchers argue that the single most effective cleanup target is the industrial fishing " +
                "industry, not consumer packaging.\n\n" +
                "The Ocean Cleanup project's System 002 has removed over 200 tonnes of plastic from the patch since deployment — a promising proof of " +
                "concept, but still a fraction of what flows in each year.",
            "plastic", "\uD83D\uDDFF",
            "The Ocean Cleanup",
            "https://theoceancleanup.com/great-pacific-garbage-patch/",
            now.minusDays(12)
        ));

        news.savePost(new NewsPost(
            "Sea otters are a keystone species for kelp forests",
            "Without otters, sea urchins explode in number and strip kelp forests bare \u2014 a single otter can protect tonnes of carbon-sequestering kelp.",
            "Sea otters eat sea urchins. Sea urchins eat kelp. When otter populations collapsed due to fur hunting in the 18th and 19th centuries, " +
                "urchin numbers exploded and entire kelp forests were converted into barren \"urchin deserts\".\n\n" +
                "Kelp forests are one of the most biologically productive ecosystems on Earth — rivalling tropical rainforests — and they lock " +
                "carbon out of the atmosphere as they grow. A 2012 study estimated that restored otter populations along the Pacific coast could " +
                "sequester an additional 4.4 to 8.7 million tonnes of carbon in kelp each year.\n\n" +
                "It's a textbook example of a trophic cascade: one species at the top of the food chain changes the structure of an entire ecosystem.",
            "species", "\uD83E\uDD9D",
            "Frontiers in Ecology and the Environment",
            "https://esajournals.onlinelibrary.wiley.com/",
            now.minusDays(14)
        ));

        news.savePost(new NewsPost(
            "Microplastics found in human blood for the first time",
            "A 2022 Dutch study detected microplastic particles in the blood of 77% of volunteers tested \u2014 and we still don't know what that means for health.",
            "In March 2022, researchers at Vrije Universiteit Amsterdam published the first evidence that microplastics can be absorbed into the " +
                "human bloodstream. They tested blood from 22 anonymous donors and found plastic particles in 17 of them — most commonly PET (water bottles), " +
                "polystyrene (food packaging), and polyethylene (shopping bags).\n\n" +
                "The health impact is still unclear. Lab studies on human cells show that microplastics can cause inflammation and oxidative stress, but " +
                "no one yet knows what happens to the particles once they're inside a living person. Do they lodge in organs? Cross the blood-brain barrier? " +
                "Build up over a lifetime?\n\n" +
                "What's certain is that microplastics are now ubiquitous — they've been found in human placentas, lung tissue, breast milk, and " +
                "freshly fallen Arctic snow.",
            "science", "\uD83E\uDDEA",
            "Environment International",
            "https://www.sciencedirect.com/journal/environment-international",
            now.minusDays(15)
        ));

        news.savePost(new NewsPost(
            "EU bans single-use plastic plates, cutlery, and straws",
            "Since July 2021, a sweeping EU directive has outlawed the ten most commonly found single-use plastic items on European beaches.",
            "The EU Single-Use Plastics Directive, which came into force in July 2021, bans the ten plastic items most frequently found on European " +
                "beaches: cutlery, plates, straws, stirrers, cotton-bud sticks, polystyrene food and drink containers, and oxo-degradable plastics.\n\n" +
                "Member states must also ensure that by 2029, 90% of plastic bottles are collected for recycling (77% by 2025), and that all " +
                "plastic bottles contain at least 25% recycled content by 2025, rising to 30% by 2030.\n\n" +
                "Early results from beach cleanups in France, Portugal, and Greece show a noticeable drop in banned items — though cigarette butts, " +
                "which are also plastic-containing but not banned, remain the single most common beach pollutant.",
            "progress", "\uD83C\uDDEA\uD83C\uDDFA",
            "European Commission",
            "https://environment.ec.europa.eu/topics/plastics/single-use-plastics_en",
            now.minusDays(17)
        ));

        news.savePost(new NewsPost(
            "Overfishing has tripled in 50 years",
            "More than a third of global fish stocks are now being fished beyond sustainable levels, up from just 10% in 1974.",
            "According to the FAO's 2022 State of World Fisheries report, the proportion of global fish stocks being exploited at unsustainable " +
                "levels has risen from 10% in 1974 to 35.4% today. That means more than a third of commercial fisheries are catching fish faster than " +
                "the populations can replenish.\n\n" +
                "Bluefin tuna, Atlantic cod, and several shark species are among the hardest hit. Beyond the ecological damage, overfishing threatens " +
                "the food security of an estimated 3 billion people who rely on fish as a primary protein source.\n\n" +
                "Marine Protected Areas, catch quotas, and banning the most destructive gear (bottom trawling over vulnerable habitats) are the three " +
                "interventions with the strongest evidence base for reversing the trend.",
            "science", "\uD83C\uDFA3",
            "FAO State of World Fisheries",
            "https://www.fao.org/state-of-fisheries-aquaculture",
            now.minusDays(19)
        ));

        news.savePost(new NewsPost(
            "Mangroves store 4x more carbon than tropical forests",
            "Per hectare, mangrove forests lock away up to four times more carbon than rainforest \u2014 yet we've destroyed 35% of them since 1980.",
            "Mangrove forests grow in the tidal zone between land and sea, tangling their roots through brackish water. They are astonishingly " +
                "effective carbon sinks: per hectare, they can sequester up to four times more carbon than a tropical rainforest, mostly in their " +
                "waterlogged soils where decomposition is slow.\n\n" +
                "They also act as natural seawalls — one study found that intact mangroves reduced property damage from the 2004 Indian Ocean tsunami " +
                "by an estimated US $65 billion.\n\n" +
                "Despite this, we've lost roughly 35% of global mangrove cover since 1980, mostly to shrimp farming, coastal development, and " +
                "palm oil plantations. Restoration efforts in Indonesia, Senegal, and the Philippines have started to turn the curve — Indonesia alone " +
                "has pledged to restore 600,000 hectares by 2030.",
            "progress", "\uD83C\uDF33",
            "Global Mangrove Alliance",
            "https://www.mangrovealliance.org/",
            now.minusDays(22)
        ));

        news.savePost(new NewsPost(
            "Noise pollution is drowning out whale song",
            "Shipping noise has doubled each decade since the 1960s, disrupting how whales find each other, mate, and navigate.",
            "Whales evolved to communicate across entire ocean basins using low-frequency calls that can travel thousands of kilometres. Industrial " +
                "shipping has filled that frequency range with constant low-frequency rumble — the acoustic equivalent of trying to have a conversation " +
                "at a rock concert.\n\n" +
                "Studies off the US East Coast have measured right whales \"shouting\" — raising their call frequency and volume to be heard over ship " +
                "traffic. Chronic noise exposure also raises stress hormone levels, disrupts feeding, and can mask the calls of predators and prey.\n\n" +
                "Solutions being trialled include slower ship speed zones in whale habitats (which cut underwater noise by up to 40%), rerouting shipping " +
                "lanes around critical breeding grounds, and retrofitting older vessels with quieter propellers.",
            "species", "\uD83D\uDC33",
            "NOAA Fisheries",
            "https://www.fisheries.noaa.gov/topic/ocean-acoustics",
            now.minusDays(24)
        ));

        news.savePost(new NewsPost(
            "Beach cleanups are turning data into policy change",
            "The Ocean Conservancy's annual cleanup has logged 350 million pieces of trash since 1986 \u2014 and that data is driving real bans.",
            "The Ocean Conservancy's International Coastal Cleanup has mobilised over 17 million volunteers across 150 countries since 1986. " +
                "The point isn't just the trash collected — it's the data. Every item is logged by type, and that dataset is one of the most-cited " +
                "sources in the world for understanding what's actually polluting our coasts.\n\n" +
                "That data has directly fed into plastic bag bans in California, straw bans in Seattle, and the EU's Single-Use Plastics Directive. " +
                "If cigarette butts top the list year after year (and they do — over 60 million logged), regulators have a hard time ignoring it.\n\n" +
                "You can join a cleanup, log your own, or use their Clean Swell app to contribute to the global dataset from anywhere.",
            "action", "\uD83D\uDC65",
            "Ocean Conservancy",
            "https://oceanconservancy.org/trash-free-seas/international-coastal-cleanup/",
            now.minusDays(26)
        ));
    }
}
