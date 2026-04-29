package com.q.quizzes;

import com.q.quizzes.model.Question;
import com.q.quizzes.model.Quiz;
import com.q.quizzes.repository.QuestionRepository;
import com.q.quizzes.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InDatabase implements CommandLineRunner 
{
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception
    {
        // Idempotent: only seed when DB is empty. Re-seeding on every
        // restart caused quiz IDs to drift (auto-increment never resets),
        // orphaning rows in user_quizzes that referenced previous IDs.
        if (quizRepository.count() > 0) return;

        // Seed Quizzes
        Quiz q1 = quizRepository.save(new Quiz(
                "Ocean Basics",
                "Learn the fundamentals of ocean ecosystems, marine life, and the importance of healthy seas.",
                "Marine Life", "Easy", 10, 1L, new Date()));

        Quiz q2 = quizRepository.save(new Quiz(
                "Plastic Pollution",
                "Discover the impact of plastic waste on marine environments and how we can reduce it.",
                "Environment", "Medium", 12, 1L, new Date()));

        Quiz q3 = quizRepository.save(new Quiz(
                "Marine Biodiversity",
                "Explore the incredible variety of life in our oceans and why protecting aquatic life matters.",
                "Marine Life", "Hard", 15, 1L, new Date()));

        Quiz q4 = quizRepository.save(new Quiz(
                "Climate Change & Oceans",
                "Understand how climate change affects ocean temperatures, currents, and ecosystems.",
                "Climate", "Medium", 14, 1L, new Date()));

        Quiz q5 = quizRepository.save(new Quiz(
                "Endangered Species",
                "Learn about threatened marine animals and what conservation efforts can help save endangered species.",
                "Conservation", "Hard", 13, 1L, new Date()));

        Quiz q6 = quizRepository.save(new Quiz(
                "Beach Cleanup Expert",
                "Master the best practices for organising and executing effective beach cleanups.",
                "Conservation", "Easy", 10, 1L, new Date()));

        // Quiz 1: Ocean Basics (10 questions) 
        Long id1 = q1.getQuizId();
        questionRepository.save(Question.trueFalse(id1,
                "The ocean covers more than 70% of Earth's surface.", true));
        questionRepository.save(Question.multiChoice(id1,
                "What is the largest ocean on Earth?",
                "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "A"));
        questionRepository.save(Question.trueFalse(id1,
                "The Pacific Ocean is the deepest ocean on Earth.", true));
        questionRepository.save(Question.multiChoice(id1,
                "Approximately what percentage of Earth's water is found in the oceans?",
                "50%", "71%", "97%", "85%", "C"));
        questionRepository.save(Question.trueFalse(id1,
                "Coral reefs are found only in warm, shallow tropical waters.", true));
        questionRepository.save(Question.multiChoice(id1,
                "What is the name of the deepest point in the ocean?",
                "Mariana Trench", "Atlantic Ridge", "Pacific Abyss", "Ocean Trench", "A"));
        questionRepository.save(Question.trueFalse(id1,
                "The ocean produces approximately 50% of the world's oxygen.", true));
        questionRepository.save(Question.multiChoice(id1,
                "Which ocean zone receives absolutely no sunlight?",
                "Sunlight zone", "Twilight zone", "Midnight zone", "Abyssal zone", "C"));
        questionRepository.save(Question.trueFalse(id1,
                "Seawater is less dense than freshwater.", false));
        questionRepository.save(Question.multiChoice(id1,
                "What are the two main forces that drive ocean currents?",
                "Wind and temperature differences", "Only the moon's gravity",
                "Fish movement", "Earthquakes only", "A"));

        //Quiz 2: Plastic Pollution (12 questions)
        Long id2 = q2.getQuizId();
        questionRepository.save(Question.trueFalse(id2,
                "Plastic takes around 450 years to decompose in the ocean.", true));
        questionRepository.save(Question.multiChoice(id2,
                "How many million tonnes of plastic enter the oceans each year?",
                "1 million", "3 million", "8 million", "20 million", "C"));
        questionRepository.save(Question.trueFalse(id2,
                "Microplastics are defined as pieces larger than 5mm in size.", false));
        questionRepository.save(Question.multiChoice(id2,
                "What percentage of ocean plastic pollution comes from land-based sources?",
                "20%", "50%", "80%", "100%", "C"));
        questionRepository.save(Question.trueFalse(id2,
                "Sea turtles often mistake plastic bags for jellyfish.", true));
        questionRepository.save(Question.multiChoice(id2,
                "What is the Great Pacific Garbage Patch?",
                "An ocean cleaning facility",
                "A large area of concentrated marine plastic debris",
                "A type of deep-sea fish",
                "A man-made island", "B"));
        questionRepository.save(Question.trueFalse(id2,
                "Single-use plastics are the biggest contributor to ocean plastic pollution.", true));
        questionRepository.save(Question.multiChoice(id2,
                "What is 'nurdle' pollution?",
                "Oil spills from tankers",
                "Tiny plastic manufacturing pellets entering waterways",
                "Untreated sewage",
                "Industrial chemical waste", "B"));
        questionRepository.save(Question.trueFalse(id2,
                "Biodegradable plastics break down instantly when they enter the ocean.", false));
        questionRepository.save(Question.multiChoice(id2,
                "Which ocean is estimated to contain the most plastic pollution?",
                "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean", "D"));
        questionRepository.save(Question.trueFalse(id2,
                "Plastic pollution only affects large marine animals like whales and turtles.", false));
        questionRepository.save(Question.multiChoice(id2,
                "What happens when marine animals ingest microplastics?",
                "Nothing harmful occurs",
                "Plastics enter the food chain and cause harm",
                "It helps fish grow faster",
                "It produces extra oxygen", "B"));

        // Quiz 3: Marine Biodiversity (15 questions)
        Long id3 = q3.getQuizId();
        questionRepository.save(Question.trueFalse(id3,
                "Coral reefs cover less than 1% of the ocean floor but support around 25% of all marine species.", true));
        questionRepository.save(Question.multiChoice(id3,
                "What is the largest living structure on Earth?",
                "Amazon Rainforest", "Great Barrier Reef", "Sahara Desert", "Mariana Trench", "B"));
        questionRepository.save(Question.trueFalse(id3,
                "The blue whale is the largest animal ever to have lived on Earth.", true));
        questionRepository.save(Question.multiChoice(id3,
                "Approximately how many marine species are currently known to science?",
                "2,000", "50,000", "240,000", "1 million", "C"));
        questionRepository.save(Question.trueFalse(id3,
                "Bioluminescence — the ability to produce light — is rare in deep-ocean creatures.", false));
        questionRepository.save(Question.multiChoice(id3,
                "What is a keystone species?",
                "A species that plays a critical role in maintaining an ecosystem",
                "Simply the largest species in an ecosystem",
                "Any endangered species",
                "The fastest-moving species in a habitat", "A"));
        questionRepository.save(Question.trueFalse(id3,
                "Sharks are considered keystone species in many marine ecosystems.", true));
        questionRepository.save(Question.multiChoice(id3,
                "Which animal can instantly change both its colour and skin texture?",
                "Tuna", "Octopus", "Humpback whale", "Bottlenose dolphin", "B"));
        questionRepository.save(Question.trueFalse(id3,
                "Sea otters play an important role in maintaining kelp forest ecosystems.", true));
        questionRepository.save(Question.multiChoice(id3,
                "What percentage of ocean species are estimated yet to be discovered?",
                "10%", "50%", "75%", "91%", "D"));
        questionRepository.save(Question.trueFalse(id3,
                "Phytoplankton forms the very base of the marine food web.", true));
        questionRepository.save(Question.multiChoice(id3,
                "Which fish uses a bioluminescent lure to attract prey in the deep sea?",
                "Anglerfish", "Goldfish", "Bluefin tuna", "Clownfish", "A"));
        questionRepository.save(Question.trueFalse(id3,
                "All species of jellyfish are dangerous and harmful to humans.", false));
        questionRepository.save(Question.multiChoice(id3,
                "What does the term 'marine endemism' describe?",
                "A species found only in one specific geographic area",
                "Ocean pollution caused by industry",
                "The process of coral bleaching",
                "Seasonal fish migration patterns", "A"));
        questionRepository.save(Question.trueFalse(id3,
                "The ocean floor remains the least explored environment on Earth.", true));

        // Quiz 4: Climate Change & Oceans (14 questions)
        Long id4 = q4.getQuizId();
        questionRepository.save(Question.trueFalse(id4,
                "Ocean temperatures have measurably risen as a result of climate change.", true));
        questionRepository.save(Question.multiChoice(id4,
                "Approximately what percentage of human-produced CO2 does the ocean absorb?",
                "10%", "25%", "30%", "50%", "C"));
        questionRepository.save(Question.trueFalse(id4,
                "Ocean acidification is caused by CO2 dissolving in seawater to form carbonic acid.", true));
        questionRepository.save(Question.multiChoice(id4,
                "What is the primary cause of coral bleaching events?",
                "Rising sea temperatures", "Plastic pollution",
                "Overfishing", "Storm damage", "A"));
        questionRepository.save(Question.trueFalse(id4,
                "Sea levels are rising due to melting ice sheets and thermal expansion of seawater.", true));
        questionRepository.save(Question.multiChoice(id4,
                "By approximately how much have global sea levels risen in the last century?",
                "5 cm", "12 cm", "21 cm", "50 cm", "C"));
        questionRepository.save(Question.trueFalse(id4,
                "Warmer ocean temperatures contribute to more powerful and intense hurricanes.", true));
        questionRepository.save(Question.multiChoice(id4,
                "What happens to global ocean circulation as Arctic ice continues to melt?",
                "It speeds up significantly", "It slows down and weakens",
                "It stops completely", "It remains unchanged", "B"));
        questionRepository.save(Question.trueFalse(id4,
                "Ocean acidification damages the shells and skeletons of shellfish and corals.", true));
        questionRepository.save(Question.multiChoice(id4,
                "Which greenhouse gas is most responsible for warming the world's oceans?",
                "Carbon dioxide (CO2)", "Nitrogen (N2)",
                "Oxygen (O2)", "Hydrogen (H2)", "A"));
        questionRepository.save(Question.trueFalse(id4,
                "Climate change is causing some fish species to migrate toward cooler, deeper waters.", true));
        questionRepository.save(Question.multiChoice(id4,
                "What is ocean deoxygenation?",
                "The gradual reduction of oxygen levels in ocean water",
                "The complete evaporation of ocean water",
                "The warming of ocean surface temperatures",
                "The acidification of seawater", "A"));
        questionRepository.save(Question.trueFalse(id4,
                "The Arctic region is warming approximately twice as fast as the global average.", true));
        questionRepository.save(Question.multiChoice(id4,
                "By how much has the average ocean pH dropped since the industrial era?",
                "0.001 units", "0.01 units", "0.1 units", "1.0 units", "C"));

        // Quiz 5: Endangered Species (13 questions) 
        Long id5 = q5.getQuizId();
        questionRepository.save(Question.trueFalse(id5,
                "The vaquita porpoise is currently the world's most endangered marine mammal.", true));
        questionRepository.save(Question.multiChoice(id5,
                "Approximately how many vaquita porpoises are estimated to remain alive today?",
                "Fewer than 10", "Around 100", "Around 500", "Around 1,000", "A"));
        questionRepository.save(Question.trueFalse(id5,
                "Sea turtles have existed on Earth for over 100 million years.", true));
        questionRepository.save(Question.multiChoice(id5,
                "What is the primary threat to sea turtle populations worldwide?",
                "Natural predators",
                "Fishing bycatch and net entanglement",
                "Infectious disease",
                "Dehydration", "B"));
        questionRepository.save(Question.trueFalse(id5,
                "The hawksbill sea turtle is listed as Critically Endangered on the IUCN Red List.", true));
        questionRepository.save(Question.multiChoice(id5,
                "What poses the greatest threat to blue whale populations today?",
                "Noise pollution alone",
                "Ship strikes and entanglement in fishing gear",
                "Competition from jellyfish",
                "Seaweed overgrowth", "B"));
        questionRepository.save(Question.trueFalse(id5,
                "The demand for shark fin soup is driving multiple shark species toward extinction.", true));
        questionRepository.save(Question.multiChoice(id5,
                "The dugong is most closely related to which other animal?",
                "Dolphins", "Manatees", "Seals", "Walruses", "B"));
        questionRepository.save(Question.trueFalse(id5,
                "All species of seahorse are currently classified as endangered.", false));
        questionRepository.save(Question.multiChoice(id5,
                "On the IUCN Red List, what does 'Critically Endangered' mean?",
                "The species faces no immediate risk",
                "The species faces an extremely high risk of extinction in the wild",
                "The species has already gone extinct",
                "The species is widespread and common", "B"));
        questionRepository.save(Question.trueFalse(id5,
                "Overfishing is one of the leading causes of marine species population decline.", true));
        questionRepository.save(Question.multiChoice(id5,
                "Which whale species is considered the most endangered whale in the world?",
                "Blue whale", "North Atlantic Right Whale",
                "Humpback whale", "Orca", "B"));
        questionRepository.save(Question.trueFalse(id5,
                "Marine protected areas (MPAs) have been shown to help endangered species recover.", true));

        // Quiz 6: Beach Cleanup Expert (10 questions)
        Long id6 = q6.getQuizId();
        questionRepository.save(Question.trueFalse(id6,
                "You should always wear protective gloves when participating in a beach cleanup.", true));
        questionRepository.save(Question.multiChoice(id6,
                "What item is most commonly found during organised beach cleanups worldwide?",
                "Cigarette butts", "Broken glass", "Metal drink cans", "Seaweed", "A"));
        questionRepository.save(Question.trueFalse(id6,
                "It is safe to pick up medical waste or syringes with bare hands during a beach cleanup.", false));
        questionRepository.save(Question.multiChoice(id6,
                "What is the correct way to handle sharp objects like broken glass during a cleanup?",
                "Leave them in place",
                "Pick them up carefully and place in a separate sealed bag",
                "Bury them in the sand",
                "Throw them back into the sea", "B"));
        questionRepository.save(Question.trueFalse(id6,
                "You can organise a beach cleanup without checking local regulations or obtaining permits.", false));
        questionRepository.save(Question.multiChoice(id6,
                "What is generally the best time to conduct a beach cleanup for maximum effectiveness?",
                "High tide", "Low tide",
                "Any time — it makes no difference", "Only at night", "B"));
        questionRepository.save(Question.trueFalse(id6,
                "You should sort recyclable materials separately from general waste during a beach cleanup.", true));
        questionRepository.save(Question.multiChoice(id6,
                "What data is most valuable to record during a beach cleanup event?",
                "Nothing needs to be recorded",
                "The types and quantities of litter collected",
                "Only large items weighing over 1kg",
                "Only plastic items", "B"));
        questionRepository.save(Question.trueFalse(id6,
                "Good volunteer coordination is a key factor in running a successful beach cleanup.", true));
        questionRepository.save(Question.multiChoice(id6,
                "After completing a beach cleanup, what is the most impactful next step?",
                "Simply go home",
                "Share your results and data with local authorities and conservation groups",
                "Hold a private celebration only",
                "Do nothing further", "B"));

        System.out.println("DB has seeded: 6 quizzes | 79 questions");
    }
}
