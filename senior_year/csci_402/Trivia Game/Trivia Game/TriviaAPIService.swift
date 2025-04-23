//
//  TriviaAPIService.swift
//  Trivia Game
//
//  Created by Jamul MacNear on 4/23/25.
//

import SwiftUI

import Foundation

struct TriviaAPIService {
    static func fetchTrivia(amount: Int, category: TriviaCategory, difficulty: TriviaDifficulty, type: TriviaType) async throws -> [TriviaQuestion] {
        var urlComponents = URLComponents(string: "https://opentdb.com/api.php")!
        
        // Always add amount parameter
        var queryItems = [URLQueryItem(name: "amount", value: "\(amount)")]
        
        // Add category parameter if not "any"
        if category != .any {
            queryItems.append(URLQueryItem(name: "category", value: "\(category.rawValue)"))
        }
        
        // Add difficulty parameter if not "any"
        if difficulty != .any {
            queryItems.append(URLQueryItem(name: "difficulty", value: difficulty.rawValue))
        }
        
        // Add type parameter if not "any"
        if type != .any {
            queryItems.append(URLQueryItem(name: "type", value: type.rawValue))
        }
        
        urlComponents.queryItems = queryItems
        
        guard let url = urlComponents.url else {
            throw URLError(.badURL)
        }
        
        let (data, response) = try await URLSession.shared.data(from: url)
        
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw URLError(.badServerResponse)
        }
        
        let decoder = JSONDecoder()
        let triviaResponse = try decoder.decode(TriviaResponse.self, from: data)
        
        if triviaResponse.responseCode != 0 {
            throw TriviaError.apiError(code: triviaResponse.responseCode)
        }
        
        return triviaResponse.results
    }
}

enum TriviaError: Error {
    case apiError(code: Int)
    case decodingError
    case networkError
    
    var message: String {
        switch self {
        case .apiError(let code):
            switch code {
            case 1: return "Not enough questions available"
            case 2: return "Invalid parameter"
            case 3: return "Session token not found"
            case 4: return "Session token has retrieved all questions"
            default: return "Unknown API error"
            }
        case .decodingError:
            return "Error parsing response"
        case .networkError:
            return "Network error"
        }
    }
}
