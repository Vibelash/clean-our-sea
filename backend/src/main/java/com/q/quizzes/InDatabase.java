package com.q.quizzes;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.q.quizzes.model.Quiz;
import com.q.quizzes.repository.QuizRepository;

import java.util.Date;

@Component
public class InDatabase implements CommandLineRunner 
{

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public void run(String... args) throws Exception 
    {

        quizRepository.deleteAll();

        quizRepository.save(new Quiz(
                "Ocean Basics",
                "Learn the fundamentals of ocean ecosystems, marine life, and the importance of healthy seas.",
                "Marine Life", "Easy", 10, 1L, new Date()));

        quizRepository.save(new Quiz(
                "Plastic Pollution",
                "Discover the impact of plastic waste on marine environments and how we can reduce it.",
                "Environment", "Medium", 12, 1L, new Date()));

        quizRepository.save(new Quiz(
                "Marine Biodiversity",
                "Explore the incredible variety of life in our oceans and why protecting aquatic life matters.",
                "Marine Life", "Hard", 15, 1L, new Date()));

        quizRepository.save(new Quiz(
                "Climate Change & Oceans",
                "Understand how climate change affects ocean temperatures, currents, and ecosystems.",
                "Climate", "Medium", 14, 1L, new Date()));

        quizRepository.save(new Quiz(
                "Endangered Species",
                "Learn about threatened marine animals and what conservation efforts can help save endangered species.",
                "Conservation", "Hard", 13, 1L, new Date()));

        quizRepository.save(new Quiz(
                "Beach Cleanup Expert",
                "Master the best practices for organising and executing effective beach cleanups.",
                "Conservation", "Easy", 10, 1L, new Date()));

        System.out.println("DB has seeded: 6 quizzes loaded.");
    }
}
