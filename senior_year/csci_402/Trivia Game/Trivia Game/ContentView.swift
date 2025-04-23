//
//  ContentView.swift
//  Trivia Game
//
//  Created by Jamul MacNear on 4/23/25.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var gameState = GameState()
    @State private var numberOfQuestions: Int = 10
    @State private var selectedCategory: TriviaCategory = .any
    @State private var selectedDifficulty: TriviaDifficulty = .any
    @State private var selectedType: TriviaType = .any
    @State private var timerEnabled: Bool = false
    @State private var timerDuration: Int = 60
    @State private var showGameView = false
    @State private var isLoading = false
    @State private var errorMessage: String? = nil
    
    var body: some View {
        NavigationStack {
            Form {
                Section(header: Text("Game Options")) {
                    Stepper("Number of Questions: \(numberOfQuestions)", value: $numberOfQuestions, in: 1...50)
                    
                    Picker("Category", selection: $selectedCategory) {
                        ForEach(TriviaCategory.allCases) { category in
                            Text(category.displayName).tag(category)
                        }
                    }
                    
                    Picker("Difficulty", selection: $selectedDifficulty) {
                        ForEach(TriviaDifficulty.allCases) { difficulty in
                            Text(difficulty.displayName).tag(difficulty)
                        }
                    }
                    
                    Picker("Question Type", selection: $selectedType) {
                        ForEach(TriviaType.allCases) { type in
                            Text(type.displayName).tag(type)
                        }
                    }
                }
                
                Section(header: Text("Timer Options")) {
                    Toggle("Enable Timer", isOn: $timerEnabled)
                    
                    if timerEnabled {
                        Stepper("Timer Duration: \(timerDuration) seconds", value: $timerDuration, in: 10...300, step: 10)
                    }
                }
                
                Section {
                    Button(action: startGame) {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle())
                        } else {
                            Text("Start Trivia Game")
                                .frame(maxWidth: .infinity, alignment: .center)
                                .font(.headline)
                        }
                    }
                    .disabled(isLoading)
                    .buttonStyle(.borderedProminent)
                    .listRowInsets(EdgeInsets())
                    .padding()
                }
                
                if let errorMessage = errorMessage {
                    Section {
                        Text(errorMessage)
                            .foregroundColor(.red)
                    }
                }
            }
            .navigationTitle("Trivia Challenge")
            .navigationDestination(isPresented: $showGameView) {
                if selectedType == .boolean {
                    TrueFalseGameView(gameState: gameState, timerEnabled: timerEnabled, timerDuration: timerDuration)
                } else {
                    MultipleChoiceGameView(gameState: gameState, timerEnabled: timerEnabled, timerDuration: timerDuration)
                }
            }
        }
    }
    
    func startGame() {
        isLoading = true
        errorMessage = nil
        gameState.resetGame()
        
        Task {
            do {
                let questions = try await TriviaAPIService.fetchTrivia(
                    amount: numberOfQuestions,
                    category: selectedCategory,
                    difficulty: selectedDifficulty,
                    type: selectedType
                )
                
                DispatchQueue.main.async {
                    gameState.questions = questions
                    isLoading = false
                    showGameView = true
                }
            } catch let error as TriviaError {
                DispatchQueue.main.async {
                    errorMessage = error.message
                    isLoading = false
                }
            } catch {
                DispatchQueue.main.async {
                    errorMessage = error.localizedDescription
                    isLoading = false
                }
            }
        }
    }
}
#Preview {
    ContentView()
}
