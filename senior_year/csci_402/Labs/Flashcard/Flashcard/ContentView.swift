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
    
    // Define the body property, required by the View protocol
    // Return any object that conforms to the View protocol
    // The body defines the display content for the view
    var body: some View {
        
        // Vertical stack (VStack) to arrange views vertically
        // Declare the look and behavior for the view's display
        VStack {
            // Image view with the system's globe icon
            Image(systemName: "star")
                .imageScale(.large) // Set the image scale to large
                .foregroundStyle(.pink) // Set the image color to the tint color
            
            // Text view displaying the "Hello, world!" string
            Text("Swift, TrunKKs!")
        }
        .padding() // Apply padding to the entire VStack
    }
}

// Preview the ContentView in the canvas
#Preview {
    ContentView()
}
