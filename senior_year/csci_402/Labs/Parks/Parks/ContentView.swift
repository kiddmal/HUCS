//
//  ContentView.swift
//  Parks
//
//  Created by Jamul MacNear on 4/22/25.
//

import SwiftUI

struct ContentView: View {
    
    @State private var parks: [Park] = []
    
    var body: some View {
        NavigationStack { // <-- Wrap the top level view in a NavigationStack
            ScrollView {
                LazyVStack {
                    ForEach(parks) { park in
                        NavigationLink(value: park) { // <-- Pass in the park associated with the park row as the value
                            ParkRow(park: park) // <-- The park row serves as the label for the NavigationLink
                        }
                    }
                }
            }
            .navigationDestination(for: Park.self) { park in // <-- Add a navigationDestination that reacts to any Park type sent from a Navigation Link
                    ParkDetailView(park: park) // <-- Create a ParkDetailView for the destination, passing in the park
                }
                .navigationTitle("National Parks") // <-- Add a navigation bar title
        }
                    .onAppear(perform: { // <-- Add onAppear modifier
                                // Do something when the view appears
                                // Create a Task instance
                                Task {
                                    await fetchParks()
                                }
                            })
                    }
                }
            private func fetchParks() async {
                // URL for the API endpoint
                // 👋👋👋 Make sure to replace {YOUR_API_KEY} in the URL with your actual NPS API Key
                // Pass in any state code you like for the stateCode parameter. For instance, stateCode=fl (Florida)
                let url = URL(string: "https://developer.nps.gov/api/v1/parks?stateCode=mi&api_key={0GFQmW1cBNGyNtH8YUcL3guaAHWtaHZGxdSYEmQe}")!
                
                // Wrap in do/catch since URLSession async can throw errors
                do {
                    
                    // Perform an asynchronous data request using URLSession
                    let (data, _) = try await URLSession.shared.data(from: url)
                    
                    // Decode json data into ParksResponse type
                    let parksResponse = try JSONDecoder().decode(ParksResponse.self, from: data)
                    
                    // Get the array of parks from the response
                    var parks = parksResponse.data
                    
                    // Print the full name of each park in the array
                    for park in parks {
                        print(park.fullName)
                    }
                    
                    // TODO: Set the parks state property
                    self.parks = parks
                            
                    
                } catch {
                    print(error.localizedDescription)
                }
            }
#Preview {
    ContentView()
}
