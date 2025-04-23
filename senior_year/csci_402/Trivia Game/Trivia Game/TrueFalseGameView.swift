//
//  TrueFalseGameView.swift
//  Trivia Game
//
//  Created by Jamul MacNear on 4/23/25.
//

import SwiftUI

struct TrueFalseGameView: View {
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
                TrueFalseQuestionView(gameState: gameState)
                
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
        .navigationTitle("True/False Quiz")
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

struct TrueFalseQuestionView: View {
    @ObservedObject var gameState: GameState
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: 20) {
                ForEach(gameState.questions) { question in
                    TrueFalseCardView(
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

struct TrueFalseCardView: View {
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
            
            HStack(spacing: 16) {
                Button(action: {
                    onAnswerSelected("True")
                }) {
                    HStack {
                        Text("True")
                            .fontWeight(.medium)
                        Spacer()
                        if selectedAnswer == "True" {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.blue)
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(
                        RoundedRectangle(cornerRadius: 8)
                            .fill(selectedAnswer == "True" ? Color.blue.opacity(0.1) : Color.gray.opacity(0.1))
                    )
                }
                
                Button(action: {
                    onAnswerSelected("False")
                }) {
                    HStack {
                        Text("False")
                            .fontWeight(.medium)
                        Spacer()
                        if selectedAnswer == "False" {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.blue)
                        }
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(
                        RoundedRectangle(cornerRadius: 8)
                            .fill(selectedAnswer == "False" ? Color.blue.opacity(0.1) : Color.gray.opacity(0.1))
                    )
                }
            }
            .foregroundColor(.primary)
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

#Preview {
    NavigationStack {
        TrueFalseGameView(
            gameState: {
                let state = GameState()
                state.questions = [
                    TriviaQuestion(
                        category: "Entertainment: Film",
                        type: "boolean",
                        difficulty: "easy",
                        question: "The movie 'The Nightmare before Christmas' was directed by Tim Burton.",
                        correctAnswer: "False",
                        incorrectAnswers: ["True"]
                    ),
                    TriviaQuestion(
                        category: "Science: Computers",
                        type: "boolean",
                        difficulty: "medium",
                        question: "FLAC stands for 'Free Lossless Audio Codec'.",
                        correctAnswer: "True",
                        incorrectAnswers: ["False"]
                    )
                ]
                return state
            }(),
            timerEnabled: true,
            timerDuration: 60
        )
    }
}
