//
//  MultipleChoiceGameView.swift
//  Trivia Game
//
//  Created by Jamul MacNear on 4/23/25.
//

import SwiftUI

struct MultipleChoiceGameView: View {
    @ObservedObject var gameState: GameState
    let timerEnabled: Bool
    let timerDuration: Int
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        VStack {
            if gameState.isTimerRunning {
                TimerView(timeRemaining: gameState.timeRemaining)
            }
            
            if gameState.showResults {
                ResultsView(gameState: gameState, dismiss: dismiss)
            } else {
                QuestionListView(gameState: gameState)
                
                Button(action: gameState.submitAnswers) {
                    Text("Submit Answers")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding()
                .disabled(gameState.userAnswers.count != gameState.questions.count)
            }
        }
        .navigationTitle("Multiple Choice Quiz")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(gameState.isTimerRunning)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("Cancel") {
                    gameState.stopTimer()
                    dismiss()
                }
            }
        }
        .onAppear {
            if timerEnabled {
                gameState.startTimer(seconds: timerDuration)
            }
        }
        .onDisappear {
            gameState.stopTimer()
        }
    }
}

struct QuestionListView: View {
    @ObservedObject var gameState: GameState
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 20) {
                ForEach(gameState.questions) { question in
                    QuestionCardView(
                        question: question,
                        selectedAnswer: gameState.userAnswers[question.question] ?? "",
                        onAnswerSelected: { answer in
                            gameState.selectAnswer(for: question, answer: answer)
                        }
                    )
                }
            }
            .padding()
        }
    }
}

struct QuestionCardView: View {
    let question: TriviaQuestion
    let selectedAnswer: String
    let onAnswerSelected: (String) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            VStack(alignment: .leading, spacing: 8) {
                Text(question.category)
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Text(question.decodedQuestion)
                    .font(.headline)
                    .fixedSize(horizontal: false, vertical: true)
                
                Text("Difficulty: \(question.difficulty.capitalized)")
                    .font(.caption)
                    .foregroundColor(difficultyColor)
            }
            
            Divider()
            
            VStack(spacing: 10) {
                ForEach(question.decodedAllAnswers, id: \.self) { answer in
                    Button(action: {
                        onAnswerSelected(answer)
                    }) {
                        HStack {
                            Text(answer)
                                .fixedSize(horizontal: false, vertical: true)
                            Spacer()
                            if selectedAnswer == answer {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundColor(.blue)
                            } else {
                                Image(systemName: "circle")
                                    .foregroundColor(.gray)
                            }
                        }
                    }
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 8)
                            .fill(selectedAnswer == answer ? Color.blue.opacity(0.1) : Color.gray.opacity(0.1))
                    )
                    .foregroundColor(.primary)
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 2)
        )
    }
    
    private var difficultyColor: Color {
        switch question.difficulty {
        case "easy":
            return .green
        case "medium":
            return .orange
        case "hard":
            return .red
        default:
            return .secondary
        }
    }
}

struct TimerView: View {
    let timeRemaining: Int
    
    var body: some View {
        HStack {
            Image(systemName: "clock")
                .foregroundColor(timeRemaining < 10 ? .red : .primary)
            
            Text("\(timeRemaining) seconds")
                .font(.headline)
                .foregroundColor(timeRemaining < 10 ? .red : .primary)
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.1), radius: 5, x: 0, y: 2)
        )
        .padding()
    }
}

struct ResultsView: View {
    @ObservedObject var gameState: GameState
    let dismiss: DismissAction
    
    var body: some View {
        VStack(spacing: 20) {
            Text("Quiz Complete!")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("You scored \(gameState.score) out of \(gameState.questions.count)")
                .font(.title)
            
            Text("Percentage: \(percentage)%")
                .font(.headline)
            
            ProgressView(value: Float(gameState.score), total: Float(gameState.questions.count))
                .tint(progressColor)
                .padding()
            
            ScrollView {
                VStack(spacing: 16) {
                    ForEach(gameState.questions) { question in
                        ResultQuestionView(
                            question: question,
                            userAnswer: gameState.userAnswers[question.question] ?? "Not answered"
                        )
                    }
                }
                .padding()
            }
            
            Button(action: {
                gameState.resetGame()
                dismiss()
            }) {
                Text("Play Again")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
            .padding()
        }
        .padding()
    }
    
    private var percentage: Int {
        guard gameState.questions.count > 0 else { return 0 }
        return Int((Double(gameState.score) / Double(gameState.questions.count)) * 100)
    }
    
    private var progressColor: Color {
        let percentage = Double(gameState.score) / Double(gameState.questions.count)
        if percentage >= 0.7 {
            return .green
        } else if percentage >= 0.4 {
            return .orange
        } else {
            return .red
        }
    }
}

struct ResultQuestionView: View {
    let question: TriviaQuestion
    let userAnswer: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(question.decodedQuestion)
                .font(.headline)
                .fixedSize(horizontal: false, vertical: true)
            
            HStack(alignment: .top) {
                Text("Your answer: ")
                    .fontWeight(.medium)
                
                Text(userAnswer)
                    .foregroundColor(userAnswer == question.decodedCorrectAnswer ? .green : .red)
            }
            .font(.subheadline)
            
            if userAnswer != question.decodedCorrectAnswer {
                HStack(alignment: .top) {
                    Text("Correct answer: ")
                        .fontWeight(.medium)
                    
                    Text(question.decodedCorrectAnswer)
                        .foregroundColor(.green)
                }
                .font(.subheadline)
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(Color(.systemGray6))
        )
    }
}

#Preview {
    NavigationStack {
        MultipleChoiceGameView(
            gameState: {
                let state = GameState()
                state.questions = [
                    TriviaQuestion(
                        category: "Entertainment: Film",
                        type: "multiple",
                        difficulty: "medium",
                        question: "Which actor played Darth Vader in the original Star Wars trilogy?",
                        correctAnswer: "David Prowse",
                        incorrectAnswers: ["James Earl Jones", "Hayden Christensen", "Sebastian Shaw"]
                    ),
                    TriviaQuestion(
                        category: "Science: Computers",
                        type: "multiple",
                        difficulty: "easy",
                        question: "What does CPU stand for?",
                        correctAnswer: "Central Processing Unit",
                        incorrectAnswers: ["Central Process Unit", "Computer Personal Unit", "Central Processor Unit"]
                    )
                ]
                return state
            }(),
            timerEnabled: true,
            timerDuration: 60
        )
    }
}
