//
//  ContentView.swift
//  Flashcard
//
//  Created by Jamul MacNear on 4/20/25.
//
// Import the SwiftUI framework in any file you want to use SwiftUI
import SwiftUI

// ContentView is a custom view that conforms to the View protocol
// Define the ContentView structure, which conforms to the View protocol
struct ContentView: View {
    
    @State private var cards: [Card] = Card.mockedCards
    
    @State private var cardsToPractice: [Card] = [] // <-- Store cards removed from cards array
    @State private var cardsMemorized: [Card] = []
    @State private var deckId: Int = 0 // <-- Fixes bug to force reload SwiftUI view
    @State private var createCardViewPresented = false
    
    // Define the body property, required by the View protocol
    // Return any object that conforms to the View protocol
    // The body defines the display content for the view
    var body: some View {
        
        // Vertical stack (VStack) to arrange views vertically
        // Declare the look and behavior for the view's display
        
        // Card deck
        ZStack {
            // Reset buttons
            VStack { // <-- VStack to show buttons arranged vertically behind the cards
                Button("Reset") { // <-- Reset button with title and action}
                    cards = cardsToPractice + cardsMemorized // <-- Reset the cards array with cardsToPractice and cardsMemorized
                    cardsToPractice = [] // <-- set both cardsToPractice and cardsMemorized to empty after reset
                    cardsMemorized = []
                    deckId += 1 // <-- Increment the deck Id
                }
                .disabled(cardsToPractice.isEmpty && cardsMemorized.isEmpty)
                
                Button("More Practice") { // <-- More Practice button with title and action
                    cards = cardsToPractice // <-- Reset the cards array with cardsToPractice
                    cardsToPractice = [] // <-- Set cardsToPractice to empty after reset
                    deckId += 1 // <-- Increment the deck Id
                }
                .disabled(cardsToPractice.isEmpty)
            }
            
            // Cards
            ForEach(0..<cards.count, id: \.self) { index in
                CardView(card: cards[index], onSwipedLeft: { // <-- Add swiped left property
                    let removedCard = cards.remove(at: index) // <-- Get and Remove the card from the cards array
                    cardsToPractice.append(removedCard) // <-- Add removed card to cards to practice array
                }, onSwipedRight: { // <-- Add swiped right property
                    let removedCard = cards.remove(at: index) // <-- Remove the card from the cards array
                    cardsMemorized.append(removedCard) // <-- Add removed card to memorized cards array
                })
                    .rotationEffect(.degrees(Double(cards.count - 1 - index) * -5))
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity) // <-- Force the ZStack frame to expand as mush as possible (the whole screen in this case)
            .overlay(alignment: .topTrailing) { // <-- Add an overlay modifier with top trailing alignment for its contents
                Button("Add Flashcard", systemImage: "plus") { // <-- Add a button to add a flashcard
                    createCardViewPresented.toggle() // <-- Toggle the createCardViewPresented value to trigger the sheet to show
                }
            }
            .animation(.bouncy, value: cards)
            .id(deckId) // <-- Add an id modifier to the main card deck ZStack
            .sheet(isPresented: $createCardViewPresented, content: {CreateFlashcardView { card in
                cards.append(card)
            }
            })
        }
    }
}
        
        // Preview the ContentView in the canvas
        #Preview {
            ContentView()
        }
