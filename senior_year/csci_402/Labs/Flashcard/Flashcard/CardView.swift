//
//  CardView.swift
//  Flashcard
//
//  Created by Jamul MacNear on 4/22/25.
//

import SwiftUI

// Card data model
struct Card: Equatable { // <-- Add conformance to Equatable protocol
    let question: String
    let answer: String
    
    static let mockedCards = [
        Card(question: "Located at the southern end of Puget Sound, what is the capitol of Washington?", answer: "Olympia"),
        Card(question: "Which city serves as the capital of Texas?", answer: "Austin"),
        Card(question: "What is the capital of New York?", answer: "Albany"),
        Card(question: "Which city is the capital of Florida?", answer: "Tallahassee"),
        Card(question: "What is the capital of Colorado?", answer: "Denver")
    ]
}

struct CardView: View {
    
    let card: Card
    
    var onSwipedLeft: (() -> Void)? // <-- Add closures to be called when user swipes left or right
    var onSwipedRight: (() -> Void)?
    @State private var isShowingQuestion = true
    @State private var offset: CGSize = .zero // <-- A state property to store the offset
    private let swipeThreshold: Double = 200 // <-- Define a swipeTheshold constant top level
    
    var body: some View {
        // Vertical stack (VStack) to arrange views vertically
        // Declare the look and behavior for the view's display
        
        ZStack {
            
            // Card background
            ZStack { // <-- Wrap existing card background in ZStack in order to position another background behind it
                
                // Back-most card background
                RoundedRectangle(cornerRadius: 25.0) // <-- Add another card backgrounf behind the original
                    .fill(offset.width < 0 ? .red : .green) // <-- Set fill based on offset (swipe left vs right)
                
                //Front-most card background (i.e. original background)
                RoundedRectangle(cornerRadius: 25.0)
                    .fill(isShowingQuestion ? .blue : .indigo)
                    .shadow(color: .black, radius: 4, x: -2, y: 2)
            }
            
            VStack(spacing: 20) {
                
                // Card type (question vs answer)
                Text(isShowingQuestion ? "Question" : "Answer")
                    .bold()
                
                // Separatror
                Rectangle()
                    .frame(height: 1)
                
                // Card text
                Text(isShowingQuestion ? card.question : card.answer)
            }
            .font(.title)
            .foregroundStyle(.white)
            .padding(40)
        }
        .frame(width: 300, height: 500)
        .opacity(3 - abs(offset.width) / swipeThreshold * 3) // <-- Fade the card out as user swipes, beginning fase in the last 1/3 to the threshold
        .rotationEffect(.degrees(offset.width / 20.0)) // <-- Add rotation when swiping
        .offset(CGSize(width: offset.width, height: 0)) // <-- Use the offset value to set the offset of the card view
        .gesture(DragGesture()
            .onChanged { gesture in // <-- onChanged called for every gesture value change, like when the drag translation changes
                let translation = gesture.translation // <-- Get the current translation value of the gesture. (CGSize with width and height)
                print(translation) // <--- Print the translation value})
                offset = translation // <-- Update the state offset property as the gesture translation changes
            }.onEnded { gesture in // <-- onEnded called when gesture ends
                
                if gesture.translation.width > swipeThreshold { // <-- Compare the gesture ended translation value to the swipeThreshold
                    print("👉🏽 Swiped right")
                    onSwipedRight?() // <-- Call swiped right closure
                } else if gesture.translation.width < -swipeThreshold {
                    print("👈🏽 Swiped left")
                    onSwipedLeft?() // <-- Call swiped left closure
                } else {
                    print("🚫 Swipe cancelled")
                    withAnimation(.bouncy) { // <-- Make updates to state managed property with animation
                        offset = .zero
                    }}
            }
        )
        .onTapGesture {
            isShowingQuestion.toggle()
        }
    }
}
        
        #Preview {
            CardView(card: Card(
                question: "Located at the southern end of Puget Sound, what is the capitol of Washington?",
                answer: "Olympia"))
        }

