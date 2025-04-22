//
//  ContentView.swift
//  Outta Mind Memory Game
//
//  Created by Jamul MacNear on 4/22/25.
//

import SwiftUI

struct ContentView: View {
    // Game state
    @State private var cards: [Card] = []
    @State private var selectedCardIndex: Int? = nil
    @State private var matchedIndices: Set<Int> = []
    @State private var isCardFlipping = false
    @State private var numberOfPairs = 8
    
    // Emoji choices
    let emojis = ["🌎", "🌚", "☀️", "🌖", "💫", "☄️", "🪐", "🌟", "🌙", "💥", "⚡️", "🚀", "⭐️", "🌞", "🔭", "🛰️", "👽", "🛸", "👾"]
    
    // Column setup for grid
    let columns = [
        GridItem(.adaptive(minimum: 80))
    ]
    
    var body: some View {
        VStack {
            Text("Memory Game")
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding()
            
            // Game status
            Text("Matches Found: \(matchedIndices.count / 2) of \(numberOfPairs)")
                .font(.headline)
                .padding(.bottom)
            
            // Number of pairs picker
            HStack {
                Text("Pairs:")
                Picker("Number of Pairs", selection: $numberOfPairs) {
                    ForEach(2...10, id: \.self) { number in
                        Text("\(number)").tag(number)
                    }
                }
                .pickerStyle(MenuPickerStyle())
                .onChange(of: numberOfPairs) { _ in
                    resetGame()
                }
            }
            .padding(.horizontal)
            
            ScrollView {
                LazyVGrid(columns: columns, spacing: 15) {
                    ForEach(0..<cards.count, id: \.self) { index in
                        CardView(card: cards[index], isMatched: matchedIndices.contains(index))
                            .aspectRatio(2/3, contentMode: .fit)
                            .onTapGesture {
                                // Handle card tap
                                handleCardTap(index: index)
                            }
                    }
                }
                .padding()
            }
            
            Button(action: resetGame) {
                Text("New Game")
                    .font(.headline)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
            }
            .padding()
            
            Spacer()
        }
        .onAppear {
            resetGame()
        }
    }
    
    // Initialize or reset the game
    func resetGame() {
        matchedIndices = []
        selectedCardIndex = nil
        isCardFlipping = false
        
        // Create pairs of cards
        var newCards: [Card] = []
        let shuffledEmojis = emojis.shuffled()
        
        for i in 0..<numberOfPairs {
            let emoji = shuffledEmojis[i]
            newCards.append(Card(id: i * 2, content: emoji, isFaceUp: false))
            newCards.append(Card(id: i * 2 + 1, content: emoji, isFaceUp: false))
        }
        
        // Shuffle the cards
        cards = newCards.shuffled()
    }
    
    // Handle the card tap logic
    func handleCardTap(index: Int) {
        // Ignore tap if card is already matched or flipping is in progress
        if matchedIndices.contains(index) || isCardFlipping || cards[index].isFaceUp {
            return
        }
        
        // First card selected
        if selectedCardIndex == nil {
            selectedCardIndex = index
            cards[index].isFaceUp = true
            return
        }
        
        // Second card selected
        guard let firstIndex = selectedCardIndex else { return }
        if firstIndex != index {
            cards[index].isFaceUp = true
            isCardFlipping = true
            
            // Check for match
            if cards[firstIndex].content == cards[index].content {
                // Match found
                matchedIndices.insert(firstIndex)
                matchedIndices.insert(index)
                selectedCardIndex = nil
                isCardFlipping = false
            } else {
                // No match, flip back after delay
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    cards[firstIndex].isFaceUp = false
                    cards[index].isFaceUp = false
                    selectedCardIndex = nil
                    isCardFlipping = false
                }
            }
        }
    }
}

// Card model
struct Card: Identifiable {
    let id: Int
    let content: String
    var isFaceUp: Bool
}

// View for individual card
struct CardView: View {
    let card: Card
    let isMatched: Bool
    
    var body: some View {
        ZStack {
            if isMatched {
                Color.clear // Card disappears when matched
            } else if card.isFaceUp {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color.white)
                RoundedRectangle(cornerRadius: 10)
                    .strokeBorder(Color.blue, lineWidth: 3)
                Text(card.content)
                    .font(.system(size: 42))
            } else {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color.blue)
            }
        }
        .rotation3DEffect(
            Angle.degrees(card.isFaceUp ? 0 : 180),
            axis: (x: 0, y: 1, z: 0)
        )
        .animation(.default, value: card.isFaceUp)
        .animation(.default, value: isMatched)
    }
}

#Preview {
    ContentView()
}

#Preview {
    ContentView()
}
