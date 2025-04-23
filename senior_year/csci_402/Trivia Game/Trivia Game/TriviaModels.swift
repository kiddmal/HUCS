//
//  TriviaModels.swift
//  Trivia Game
//
//  Created by Jamul MacNear on 4/23/25.
//

import SwiftUI

import Foundation

// Structure for API response
struct TriviaResponse: Codable {
    let responseCode: Int
    let results: [TriviaQuestion]
    
    enum CodingKeys: String, CodingKey {
        case responseCode = "response_code"
        case results
    }
}

// Structure for trivia questions
struct TriviaQuestion: Codable, Identifiable, Hashable {
    let category: String
    let type: String
    let difficulty: String
    let question: String
    let correctAnswer: String
    let incorrectAnswers: [String]
    
    // Computed property for all answers (shuffled)
    var allAnswers: [String] {
        var answers = incorrectAnswers
        answers.append(correctAnswer)
        return answers.shuffled()
    }
    
    // Computed ID for Identifiable conformance
    var id: String {
        question
    }
    
    enum CodingKeys: String, CodingKey {
        case category
        case type
        case difficulty
        case question
        case correctAnswer = "correct_answer"
        case incorrectAnswers = "incorrect_answers"
    }
    
    // HTML decoding for questions and answers
    var decodedQuestion: String {
        question.htmlDecoded
    }
    
    var decodedCorrectAnswer: String {
        correctAnswer.htmlDecoded
    }
    
    var decodedIncorrectAnswers: [String] {
        incorrectAnswers.map { $0.htmlDecoded }
    }
    
    var decodedAllAnswers: [String] {
        allAnswers.map { $0.htmlDecoded }
    }
    
    // Hashable conformance
    func hash(into hasher: inout Hasher) {
        hasher.combine(question)
    }
    
    static func == (lhs: TriviaQuestion, rhs: TriviaQuestion) -> Bool {
        lhs.question == rhs.question
    }
}

// MARK: - API Option Enums
enum TriviaCategory: Int, CaseIterable, Identifiable {
    case any = 0
    case generalKnowledge = 9
    case books = 10
    case film = 11
    case music = 12
    case musicals = 13
    case television = 14
    case videoGames = 15
    case boardGames = 16
    case science = 17
    case computers = 18
    case mathematics = 19
    case mythology = 20
    case sports = 21
    case geography = 22
    case history = 23
    case politics = 24
    case art = 25
    case celebrities = 26
    case animals = 27
    case vehicles = 28
    case comics = 29
    case gadgets = 30
    case anime = 31
    case cartoons = 32
    
    var id: Int { rawValue }
    
    var displayName: String {
        switch self {
        case .any: return "Any Category"
        case .generalKnowledge: return "General Knowledge"
        case .books: return "Books"
        case .film: return "Film"
        case .music: return "Music"
        case .musicals: return "Musicals & Theatre"
        case .television: return "Television"
        case .videoGames: return "Video Games"
        case .boardGames: return "Board Games"
        case .science: return "Science & Nature"
        case .computers: return "Computers"
        case .mathematics: return "Mathematics"
        case .mythology: return "Mythology"
        case .sports: return "Sports"
        case .geography: return "Geography"
        case .history: return "History"
        case .politics: return "Politics"
        case .art: return "Art"
        case .celebrities: return "Celebrities"
        case .animals: return "Animals"
        case .vehicles: return "Vehicles"
        case .comics: return "Comics"
        case .gadgets: return "Gadgets"
        case .anime: return "Anime & Manga"
        case .cartoons: return "Cartoon & Animations"
        }
    }
}

enum TriviaDifficulty: String, CaseIterable, Identifiable {
    case any = "any"
    case easy = "easy"
    case medium = "medium"
    case hard = "hard"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .any: return "Any Difficulty"
        case .easy: return "Easy"
        case .medium: return "Medium"
        case .hard: return "Hard"
        }
    }
}

enum TriviaType: String, CaseIterable, Identifiable {
    case any = "any"
    case multiple = "multiple"
    case boolean = "boolean"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .any: return "Any Type"
        case .multiple: return "Multiple Choice"
        case .boolean: return "True / False"
        }
    }
}

// MARK: - Helper Extensions
extension String {
    var htmlDecoded: String {
        guard let data = data(using: .utf8) else { return self }
        
        let options: [NSAttributedString.DocumentReadingOptionKey: Any] = [
            .documentType: NSAttributedString.DocumentType.html,
            .characterEncoding: String.Encoding.utf8.rawValue
        ]
        
        if let attributedString = try? NSAttributedString(data: data, options: options, documentAttributes: nil) {
            return attributedString.string
        }
        
        return self
    }
}

// MARK: - Game State Object
class GameState: ObservableObject {
    @Published var questions: [TriviaQuestion] = []
    @Published var userAnswers: [String: String] = [:]
    @Published var score: Int = 0
    @Published var showResults: Bool = false
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    @Published var timeRemaining: Int = 60
    @Published var isTimerRunning: Bool = false
    @Published var timerExpired: Bool = false
    
    var timer: Timer?
    
    func startTimer(seconds: Int) {
        timeRemaining = seconds
        isTimerRunning = true
        timerExpired = false
        
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { [weak self] _ in
            guard let self = self else { return }
            
            if self.timeRemaining > 0 {
                self.timeRemaining -= 1
            } else {
                self.timerExpired = true
                self.isTimerRunning = false
                self.timer?.invalidate()
                self.submitAnswers()
            }
        }
    }
    
    func stopTimer() {
        isTimerRunning = false
        timer?.invalidate()
        timer = nil
    }
    
    func selectAnswer(for question: TriviaQuestion, answer: String) {
        userAnswers[question.question] = answer
    }
    
    func submitAnswers() {
        score = 0
        
        for question in questions {
            if let userAnswer = userAnswers[question.question],
               userAnswer == question.decodedCorrectAnswer {
                score += 1
            }
        }
        
        showResults = true
    }
    
    func resetGame() {
        questions = []
        userAnswers = [:]
        score = 0
        showResults = false
        isLoading = false
        errorMessage = nil
        timeRemaining = 60
        isTimerRunning = false
        timerExpired = false
        stopTimer()
    }
}
