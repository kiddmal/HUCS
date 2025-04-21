package com.example.disasterresponseapp10.adapters

class EmergencyContactsSection : BaseCommunicationSection() {
    override fun getTitle() = "Emergency Contacts"

    override fun onMessageSent(message: String) {
        viewModel.sendNotification(message)
    }
}
