//
//  AppDelegate.swift
//  lab-insta-parse
//
//  Created by Charlie Hieger on 10/29/22.
//

import UIKit
import ParseSwift

// TODO: Pt 1 - Import Parse Swift

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.

        // TODO: Pt 1 - Initialize Parse SDK
        ParseSwift.initialize(applicationId: "3xeFywaNOjtU39b6a1MxRiUrEh42abSAa3lO1lqY", clientKey: "KDcqlojXP12wJXpljHqrLkCvZmcV58foEiO4BgXo", serverURL: URL(string: "https://parseapi.back4app.com")!)
        // https://github.com/parse-community/Parse-Swift/blob/main/ParseSwift.playground/Sources/Common.swift


        // TODO: Pt 1: - Instantiate and save a test parse object to your server
        // Instantiate the test parse object
        //var score = GameScore()
        //score.playerName = "Kingsley"
        //score.points = 13
        
        // Save to your server asynchrounously (preferred way) - Performs work on background queue and returns
        // If no callbackQueue is specified it returns to main queue.
        //score.save { result in
            //switch result {
            //case .success(let savedScore):
                //print("✅ Parse Object SAVED!: Player: \(String(describing: savedScore.playerName)), Score: \(String(describing: savedScore.points))")
            //case .failure(let error):
            //assertionFailure("Error saving: \(error)")
            //}}
        // https://github.com/parse-community/Parse-Swift/blob/3d4bb13acd7496a49b259e541928ad493219d363/ParseSwift.playground/Pages/1%20-%20Your%20first%20Object.xcplaygroundpage/Contents.swift#L121


        return true
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
}

// TODO: Pt 1 - Create Test Parse Object
// Create your own value type 'ParseObject'.
//struct GameScore: ParseObject {
    //These are required by ParseObject
    //var objectId: String?
    //var createdAt: Date?
    //var updatedAt: Date?
    //var ACL: ParseACL?
    //var originalData: Data?
    
    // Your own custom properties.
    // All custom properties must be optional.
    //var playerName: String?
    //var points: Int?
//}
    
// Sample Usage
//
// var score = GameScore()
// score.playerName = "Kingsley"
// score.points = 13

// OR Implement a cusotnm initializer (OPTIONAL i.e. NOT REQUIRED)
// It's recommended to place custom initializers in an extension to preserve the memberwise initializer.
//extension GameScore {
    
    // Use the init to set your custome properties
    // NOTE: Properties in custom init are NOT required to be optional
    //init(playerName: String, points: Int) {
        //self.playerName = playerName
        //self.points = points
    //}
//}

    
// Sample Usage
//
// let score = GameScore(playerName: "Kingsley", points: 13)

// https://github.com/parse-community/Parse-Swift/blob/3d4bb13acd7496a49b259e541928ad493219d363/ParseSwift.playground/Pages/1%20-%20Your%20first%20Object.xcplaygroundpage/Contents.swift#L33


