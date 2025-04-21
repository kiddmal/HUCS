//
//  FlashcardApp.swift
//  Flashcard
//
//  Created by Jamul MacNear on 4/20/25.
//

// Import SwiftUI in any file you want to use SwiftUI
import SwiftUI

// The @main attribute indicates that FlashcardStarterApp is the entry point for the app.
// @main marks main entry point
@main
struct FlashcardApp: App {
    
    // The 'body' property is required for any SwiftUI App. It defines the content of the app.
    // The body property defines the main structure of the app (a scene builder)
    var body: some Scene {
        // A Scene represents a single user interface within the app.
        // Scene is a protocol
        
        WindowGroup {
            // The WindowGroup represents the main window of the app.
            // A WindowGroup represents a top-level window in the app.
            // WindowGroup is used to manage the lifecycle and presentation of these windows in your app.
            // The root view of the app do display on launch.
            // Similar to the "initial view controller" in a UIKit storyboard.
            
            ContentView()
            // ContentView is the main view or the root view of the app.
            // This is where you start defining the structure and content of your app's user interface.
        }
    }
}
