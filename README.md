
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

### Commands:
- **/worldshop** - Opens the worldshop GUI

## Contributing

### Guidelines

- **Branching Strategy**: Create development branches from issues. Each branch should be named after the corresponding issue for clarity and organization.
- **Issue Weighting**: Weight issues using Fibonacci story points (1, 2, 3, 5, 8, 13, 21, etc.) to estimate their complexity and effort required.

### Pull Request Process

1. Ensure all tests pass.
1. Submit a pull request from your feature branch to the develop branch.
1. Include a detailed description of the changes and link to the relevant issue.
1. Request a review from at least one team member.

### Commit Message Guidelines

- Use the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) style.
- Example: `feat: add new feature` or `fix: resolve issue with login`.

### Reporting Issues

- Use GitHub to report bugs or suggest features. Mark with appropriate flair/tag.
- Provide detailed information and steps to reproduce the issue.

### Documentation

- Update the documentation for any changes in the codebase.
- Add new documentation for any new features.

## License

This software is licensed under the WorldShop License.

You are free to use, modify, and distribute this software for **non-commercial purposes**. However, for **commercial use** or distribution, you must obtain a separate commercial license from OneBiteAidan.

To view the full license, see the [LICENSE](./LICENSE) file included in this repository.