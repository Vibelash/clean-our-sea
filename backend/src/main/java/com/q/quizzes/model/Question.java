package com.q.quizzes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK to quizzes — plain Long, no Hibernate join
    private Long quizId;

    @Column(length = 1000)
    private String questionText;

    // TRUE/FALSE and MULTIPLE_CHOICE
    private String type;

    private String optionA;
    private String optionB;
    private String optionC;  // null for true/false
    private String optionD;  // null for true/false

    // Correct answer letter: "A", "B", "C", or "D"
    private String correctAnswer;

    public Question() {}

    public Question(Long quizId, String questionText, String type,
                    String optionA, String optionB, String optionC, String optionD,
                    String correctAnswer) 
    {
        this.quizId        = quizId;
        this.questionText  = questionText;
        this.type          = type;
        this.optionA       = optionA;
        this.optionB       = optionB;
        this.optionC       = optionC;
        this.optionD       = optionD;
        this.correctAnswer = correctAnswer;
    }

    // True/false question
    public static Question trueFalse(Long quizId, String text, boolean answer) 
    {
        return new Question(quizId, text, "TRUE_FALSE", "True", "False", null, null, answer ? "A" : "B");
    }

    //Multiple Choice question
    public static Question multiChoice(Long quizId, String text,
                                       String a, String b, String c, String d,
                                       String correct) 
    {
        return new Question(quizId, text, "MULTIPLE_CHOICE", a, b, c, d, correct);
    }

    public Long   getId()                          { return id; }
    public void   setId(Long id)                   { this.id = id; }

    public Long   getQuizId()                      { return quizId; }
    public void   setQuizId(Long quizId)           { this.quizId = quizId; }

    public String getQuestionText()                { return questionText; }
    public void   setQuestionText(String t)        { this.questionText = t; }

    public String getType()                        { return type; }
    public void   setType(String type)             { this.type = type; }

    public String getOptionA()                     { return optionA; }
    public void   setOptionA(String optionA)       { this.optionA = optionA; }

    public String getOptionB()                     { return optionB; }
    public void   setOptionB(String optionB)       { this.optionB = optionB; }

    public String getOptionC()                     { return optionC; }
    public void   setOptionC(String optionC)       { this.optionC = optionC; }

    public String getOptionD()                     { return optionD; }
    public void   setOptionD(String optionD)       { this.optionD = optionD; }

    public String getCorrectAnswer()               { return correctAnswer; }
    public void   setCorrectAnswer(String a)       { this.correctAnswer = a; }
}
