# 1) CRC Cards :

| **Class Name:** User                                                     |
|--------------------------------------------------------------------------|
| **Parent Class :** Object                                                 |
| **Subclasses :** None                                                     |

| **Responsibilities:**                             | **Collaborators:**         |
|--------------------------------------------------|----------------------------|
| • Store user's profile and login credentials     | • Transaction              |
| • Link user to their transactions                | • UserService              |
| • Support authentication and session mapping     |                            | 

<br><br>

| **Class Name:** Stock |
|------------------------|
| **Parent Class :** Object |
| **Subclasses :** None |

| **Responsibilities:**                                  | **Collaborators:**   |
|--------------------------------------------------------|----------------------|
| • Represent tradable stock with a symbol and name      | • Transaction        |
| • Store current stock price                            | • StockService       |
| • Provide reference for buy/sell operations            |                      |

<br><br>

| **Class Name:** Transaction |
|-----------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                                  | **Collaborators:**         |
|--------------------------------------------------------|----------------------------|
| • Represent a single buy or sell operation             | • User                     |
| • Store quantity, stock, user, and price               | • Stock                    |
| • Provide data for transaction history                 | • TransactionService       |

<br><br>

| **Class Name:** UserService |
|-----------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                                         | **Collaborators:**         |
|---------------------------------------------------------------|----------------------------|
| • Handle user registration and authentication                 | • User                     |
| • Integrate with Spring Security                              | • UserRepository           |
| • Provide user lookup for transactions                        | • UserDetailsService       |

<br><br>

| **Class Name:** StockService |
|------------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                                     | **Collaborators:**   |
|-----------------------------------------------------------|----------------------|
| • Provide stock lookup and current price                  | • Stock              |
| • Handle stock creation and updates                       | • StockRepository    |
| • Support simulated or real-time pricing                  |                      |

<br><br>

| **Class Name:** TransactionService |
|------------------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                                               | **Collaborators:**            |
|---------------------------------------------------------------------|-------------------------------|
| • Handle buy/sell logic                                             | • TransactionRepository       |
| • Validate stock and user data                                     | • UserService                 |
| • Save transaction records to the database                         | • StockService                |
|                                                                     | • Transaction, User, Stock    |


<br><br> 


| **Class Name:** UserController |
|--------------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                             | **Collaborators:**   |
|--------------------------------------------------|----------------------|
| • Handle login, logout, and user dashboard       | • UserService        |
| • Route HTTP requests related to users           |                      |

<br><br>

| **Class Name:** TransactionController |
|---------------------------------------|
| **Parent Class (if any):** Object |
| **Subclasses (if any):** None |

| **Responsibilities:**                                         | **Collaborators:**         |
|---------------------------------------------------------------|----------------------------|
| • Handle HTTP requests for buy/sell                           | • TransactionService       |
| • Receive form input from UI                                  | • UserService              |
| • Invoke service methods and redirect views                   | • StockService             |

<br><br>

| **Class Name:** UserRepository |
|--------------------------------|
| **Parent Class (if any):** JpaRepository<User, Long> |
| **Subclasses (if any):** None |

| **Responsibilities:**                                 | **Collaborators:**   |
|--------------------------------------------------------|----------------------|
| • Provide database access to User entities            | • UserService        |
| • Find users by username or ID                        |                      |

<br><br>

| **Class Name:** TransactionRepository |
|---------------------------------------|
| **Parent Class (if any):** JpaRepository<Transaction, Long> |
| **Subclasses (if any):** None |

| **Responsibilities:**                                       | **Collaborators:**     |
|-------------------------------------------------------------|------------------------|
| • Save and retrieve Transaction entities                    | • TransactionService   |
| • Provide filtering by user or stock                        |                        |


<br><br>

| **Class Name:** StockRepository |
|---------------------------------|
| **Parent Class (if any):** JpaRepository<Stock, Long> |
| **Subclasses (if any):** None |

| **Responsibilities:**                              | **Collaborators:**   |
|-----------------------------------------------------|----------------------|
| • Provide database access to Stock entities         | • StockService       |
| • Lookup stocks by symbol                           |                      |


<br><br>



