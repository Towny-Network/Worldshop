
> [!NOTE]
> This document is a work in progress and is not yet complete.

# WorldShop

WorldShop is a Minecraft plugin written using the PaperMC API. The purpose of this plugin is to reduce the clutter of traditional chest shops and to prevent an in-game economy with rampant inflation. These issues are solved by having a centralized trade shop that doesn't take up physical space in the world and also by removing the traditional economy by replacing it with an entirely barter-based one.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Installation

The use of JetBrains IntellJ IDEA is recommended for development of this plugin and this guide will focus on that. The IntelliJ Community Edition installer can be found [here](https://www.jetbrains.com/idea/download/) (You'll need to scroll down to get to the community edition. 

1. Clone this repository via Git.
2. Open the project using IntelliJ.
3. Compile using Maven by clicking the 'm' in the top right, clicking the lifecycle dropdown menu, and building with 'install'.
4. The compiled plugin will be the 'target' folder in the base directory of the project.

## Usage

//TODO Instructions on how to use the project and any relevant examples.

## Contributing

### Guidelines

- **Branching Strategy**: Create development branches from issues. Each branch should be named after the corresponding issue for clarity and organization.
- **Issue Weighting**: Weight issues using Fibonacci story points (1, 2, 3, 5, 8, 13, 21, etc.) to estimate their complexity and effort required.

### Testing

**//TODO: Temporary guidelines for testing. Testing not implemented yet**


- Run tests with: `npm test`
- Add new tests for any new features or bug fixes.

### Pull Request Process

1. Ensure all tests pass.
1. Submit a pull request from your feature branch to the main branch.
1. Include a detailed description of the changes and link to the relevant issue.
1. Request a review from at least one team member.

### Commit Message Guidelines

- Use the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) style.
- Example: `feat: add new feature` or `fix: resolve issue with login`.

### Reporting Issues

- Use GitLab to report bugs or suggest features. Mark with appropriate flair/tag.
- Provide detailed information and steps to reproduce the issue.

### Documentation

- Update the documentation for any changes in the codebase.
- Add new documentation for any new features.

## License





Features to add in the future
- Make nation options like front page nation items 
- Some customizable options (inventory texture, thank you messages, etc).
- Simple database lookup website for staff to use to confirm trades n such
- A discord bot that does the same as above ^^


## Error Codes

> All WorldShop Error Codes are Prefixed with "WS". (i.e. WS0000)

- 0001: While buying an item, the player did not have enough items to proceed w/ purchase even though it was previously checked that they did.
- 0002: Player attempted to sell an item without it being in their inventory.
- 0003: No trade was found while searching w/ display item.