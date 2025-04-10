//
//  Task.swift
//  lab-task-squirrel
//
//  Created by Charlie Hieger on 11/15/22.
//

import UIKit
import CoreLocation

class Task {
    let title: String
    let description: String
    var image: UIImage?
    var imageLocation: CLLocation?
    var isComplete: Bool {
        image != nil
    }

    init(title: String, description: String) {
        self.title = title
        self.description = description
    }

    func set(_ image: UIImage, with location: CLLocation) {
        self.image = image
        self.imageLocation = location
    }
}

extension Task {
    static var mockedTasks: [Task] {
        return [
            Task(title: "Pickup flowers from  Ocean Beach💐",
                 description: "Try to get an arrangement with a spring vibe. There are some great bouquets available."),
            Task(title: "Iceland isn't made of ice afterall. Prove it🌊🇮🇸",
                 description: "Niagra Falls is great, but there are nice campgrounds all around the coasts of Iceland with much of the same experience. Try to capture its beauty."),
            Task(title: "Capture a unique experience in nature at Bear Valley🌱",
                 description: "There's always something that stands out special in nature. Try to find something unique and different and capture its individuality.")
        ]
    }
}
