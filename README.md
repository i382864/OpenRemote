# OpenRemote UI Redesign Proof of Concept (Vaadin)

## Project Description

This repository contains a frontend-only proof of concept built using the Vaadin framework. Its purpose is to demonstrate a proposed redesign of the OpenRemote Manager user interface, based on specific Figma designs aimed at improving the desktop user experience.

The demo focuses on key areas of the OpenRemote UI:
*   **Asset Page:** Showcasing a redesigned layout, notably utilizing a **3-column structure** to improve information density and usability for desktop screens, compared to the current 2-column approach.
*   **Rule Page:** A representation of the Rules management interface based on the new design concept.
*   **Insight Page:** A representation of the Insights/dashboard area based on the new design concept.

This project is intended as a visual prototype and a technical evaluation tool for Vaadin's suitability, not a functional application integrated with a backend or database.

## Project Context

OpenRemote is seeking to enhance the desktop user experience of its Manager UI. The current interface, largely adapted from a mobile-first design pattern (using Material Design and Lit), has limitations for desktop use. This project is a response to the challenge of exploring alternative frontend frameworks and proposing design adjustments to create a more user-friendly, scalable, and visually uncluttered interface specifically for professional users in B2B IoT markets.

This demo serves as one deliverable in a project evaluating UI frameworks (like Vaadin, React, MaterialUI alternatives) and proposing design improvements for OpenRemote's platform.

## Features

*   Frontend implementation of proposed Asset, Rule, and Insight pages.
*   Demonstration of a **3-column layout** for the Asset viewing page.
*   Implementation based on specific Figma UI design proposals.
*   Built using the Vaadin framework.
*   Static/simulated data for demonstration purposes (no backend integration).

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Java Development Kit (JDK) 11 or higher installed.
*   Maven installed.

### Installation and Running

1.  **Clone the repository:**
    ```bash
    git clone <repository_url>
    cd <repository_directory>
    ```
    *(Replace `<repository_url>` and `<repository_directory>` with the actual repository details)*

2.  **Run the application:**
    Use the Maven Spring Boot plugin to start the embedded server.
    ```bash
    mvn spring-boot:run
    ```

3.  **Access the application:**
    Once the command successfully starts the application (you'll see logs indicating the server is running), open your web browser and go to:
    ```
    http://localhost:8080
    ```

The Vaadin application will load and display the demo UI.

## Scope and Limitations

This project is strictly a **frontend demonstration**:

*   **No Backend:** It does not connect to any live OpenRemote backend services, databases, or APIs.
*   **Static/Simulated Data:** All data displayed (assets, rules, insights) is hardcoded or simulated within the frontend code for visualization purposes only. Interactions like clicking buttons or navigating might show UI changes but do not perform real operations.
*   **Design Proof:** It demonstrates a proposed design concept and evaluates the framework's capability to implement it. It does not cover all functionalities of the OpenRemote Manager.

## Technologies Used

*   Vaadin
*   Java
*   Spring Boot
*   Maven

## Design Highlights

The core design improvement showcased is the introduction of a 3-column layout on the Asset page. This change aims to enhance desktop usability by providing a more organized and potentially more information-rich view of asset details, attributes, and related information simultaneously without excessive scrolling or navigation.

## For OpenRemote Stakeholders

This demo is a deliverable from a project team investigating UI framework alternatives and proposing design enhancements for the OpenRemote Manager. It specifically addresses the challenge of improving the desktop experience and serves as a tangible example of implementing a redesigned interface using Vaadin, based on the evaluated design proposals. Your feedback on the design and the framework's capabilities as demonstrated here is highly valued.

## License

[Specify your project's license here, e.g., MIT, Apache 2.0, etc. If you don't have one yet, consider adding one. For example: `Distributed under the MIT License. See LICENSE for more information.`]

## Contact / Team

[Optional: Add contact information or list the team members involved if desired. Example: `This project was developed by [Team Name/Members]. For inquiries, please contact [Email Address or GitHub Usernames].`]