# Sequential composition for reacl-c

## What is this?
This repo hosts the code for my bachelor thesis ["Compositions of UI elements in reacl-c"](https://github.com/marlonschlosshauer/thesis). It adds support for sequential composition to [reacl-c](https://github.com/active-group/reacl-c/).

## API

| Name          |  Signature                          |
|---------------|-------------------------------------|
| `then`        | `[(a, Item)] -> Prog b`             |
| `return`      | `a -> Prog a`                       |
| `runner`      | `Prog a -> Item`                    |
| `show`        | `Prog a -> Item`                    |
| `make-commit` | `a -> Commit a`                     |
| `-runner`     | `[(a, Item)] -> Item`               |
| `-then`       | `Prog a -> (a -> Prog b) -> Prog b` |


Check out [my thesis](https://github.com/marlonschlosshauer/thesis) for more information on what each function does.

## Installation

1. Clone the repo: ``git clone git@github.com:marlonschlosshauer/thesis-code.git``
2. Install dependencies: ``npm i``
3. Jack into your REPL, choose the ``:frontend`` build.
4. Open ``http://localhost:8080`` after compliation is complete.

## Requirements

Clojure and ClojureScript and a REPL.
