<div align="center">



\# 🚗 SalesRadar



\### AI-Powered Automotive Sales Analytics Platform



!\[Spring Boot](https://img.shields.io/badge/Spring%20Boot%203-6DB33F?style=for-the-badge\&logo=spring\&logoColor=white)

!\[Spring AI](https://img.shields.io/badge/Spring%20AI-6DB33F?style=for-the-badge\&logo=spring\&logoColor=white)

!\[Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge\&logo=python\&logoColor=white)

!\[MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge\&logo=mysql\&logoColor=white)

!\[Streamlit](https://img.shields.io/badge/Streamlit-FF4B4B?style=for-the-badge\&logo=streamlit\&logoColor=white)

!\[scikit-learn](https://img.shields.io/badge/scikit--learn-F7931E?style=for-the-badge\&logo=scikit-learn\&logoColor=white)

!\[Ollama](https://img.shields.io/badge/Ollama-000000?style=for-the-badge\&logo=ollama\&logoColor=white)



\*\*SalesRadar\*\* bridges Java's enterprise backend power with Python's data science ecosystem —

giving automotive dealers real-time insights, AI-driven answers,

and ML-based sales forecasting in one unified dashboard.



</div>



\---



\## 🎯 What Makes SalesRadar Different?



> Most analytics tools are either backend-heavy OR data-science focused.

> \*\*SalesRadar combines both\*\* — Spring Boot 3 handles enterprise-grade REST APIs \& data ingestion,

> while Python/Streamlit delivers interactive visualizations \& ML predictions.

> Spring AI + Ollama enables natural language querying on top of real business data.



\---



\## 📸 Screenshots



\### 📤 Upload Data

!\[Upload](screenshots/upload.png)



\### 📈 Yearly Analytics

!\[Yearly](screenshots/yearly.png)



\### 📊 Deep Analytics — Brand \& Color wise

!\[Deep Analytics](screenshots/deep.png)

!\[Deep Analytics 2](screenshots/deep2.png)



\### 🔮 Sales Prediction (ML)

!\[Prediction](screenshots/prediction.png)

!\[Prediction 2](screenshots/prediction2.png)



\### 🤖 AI Insights

!\[AI](screenshots/AI.png)



\---



\## ✨ Features



| Module | Description |

|--------|-------------|

| 📤 \*\*CSV Upload\*\* | Drag \& drop CSV — auto clears old data, fresh analysis every time |

| 🎯 \*\*KPI Summary\*\* | 9 business KPIs in one glance — brand, color, state, avg price \& more |

| 📈 \*\*Yearly Analytics\*\* | Year-wise sales trends with interactive line \& bar charts |

| 📅 \*\*Monthly Analytics\*\* | Month-wise breakdown for any selected year |

| 📊 \*\*Deep Analytics\*\* | Brand / Color / State wise donut pie \& bar charts |

| 🔮 \*\*ML Prediction\*\* | scikit-learn Linear Regression forecasting for next 1-5 years |

| 🤖 \*\*AI Insights\*\* | Natural language querying via Spring AI + Ollama LLM |

| 📄 \*\*PDF Reports\*\* | Auto-generated reports with metrics \& data tables via iTextPDF |



\---



\## 🏗️ Architecture



```

┌─────────────────────────────────┐

│     Streamlit Frontend          │

│  (Python + Plotly + sklearn)    │

└────────────┬────────────────────┘

&#x20;            │ HTTP REST

┌────────────▼────────────────────┐

│   Spring Boot 3 REST APIs       │

│   (Java — Controller/Service/   │

│         Repository Layer)       │

└──────┬──────────────┬───────────┘

&#x20;      │              │

┌──────▼──────┐ ┌─────▼──────────┐

│   MySQL DB  │ │  Ollama LLM    │

│  (JPA +     │ │  (Spring AI    │

│  Hibernate) │ │   Integration) │

└──────┬──────┘ └────────────────┘

&#x20;      │

┌──────▼──────┐

│  iTextPDF   │

│ (PDF Report │

│ Generator)  │

└─────────────┘

```



\---



\## 🛠️ Tech Stack



\### Backend (Java)

| Technology | Purpose |

|-----------|---------|

| \*\*Spring Boot 3\*\* | REST API framework |

| \*\*Spring AI\*\* | LLM integration layer |

| \*\*Ollama\*\* | Local LLM (runs completely offline) |

| \*\*Spring Data JPA + Hibernate\*\* | ORM \& database operations |

| \*\*MySQL\*\* | Relational database |

| \*\*iTextPDF 5\*\* | Automated PDF report generation |

| \*\*Apache Commons CSV\*\* | CSV parsing \& data ingestion |



\### Frontend \& Data Science (Python)

| Technology | Purpose |

|-----------|---------|

| \*\*Streamlit\*\* | Interactive web dashboard |

| \*\*Plotly Express\*\* | Charts \& visualizations |

| \*\*scikit-learn\*\* | Linear Regression ML model |

| \*\*Pandas + NumPy\*\* | Data processing \& manipulation |

| \*\*Requests\*\* | REST API communication |



\---



\## 🚀 How to Run



\### Prerequisites

\- Java 17+

\- Python 3.8+

\- MySQL 8+

\- Ollama installed — \[ollama.ai](https://ollama.ai)



\### 1. Database Setup

```sql

CREATE DATABASE carsales;

```



\### 2. Configure Backend

Edit `backend/src/main/resources/application.yml`:

```yaml

spring:

&#x20; datasource:

&#x20;   url: jdbc:mysql://localhost:3306/carsales

&#x20;   username: your\_username

&#x20;   password: your\_password

```



\### 3. Run Backend

```bash

cd backend

./mvnw spring-boot:run

```



\### 4. Install Python Dependencies

```bash

cd frontend

pip install streamlit requests pandas plotly scikit-learn numpy

```



\### 5. Run Frontend

```bash

streamlit run app.py

```



\### 6. Access

```

Dashboard  →  http://localhost:8501

API Base   →  http://localhost:8080/api

```



\---



\## 📡 API Endpoints



| Method | Endpoint | Description |

|--------|----------|-------------|

| `POST` | `/api/car-sales/upload-csv` | Upload CSV file |

| `DELETE` | `/api/car-sales/clear` | Clear all data |

| `GET` | `/api/car-sales/yearly-count` | Yearly sales data |

| `GET` | `/api/car-sales/monthly-count?year=2024` | Monthly sales data |

| `GET` | `/api/car-sales/brand-count` | Brand wise count |

| `GET` | `/api/car-sales/color-count` | Color wise count |

| `GET` | `/api/car-sales/state-count` | State wise count |

| `GET` | `/api/car-sales/kpi-summary` | All 9 KPI metrics |

| `GET` | `/api/report/download` | Download PDF report |

| `POST` | `/api/ai/ask` | AI natural language query |



\---



\## 📁 Project Structure



```

SalesRadar/

├── backend/                          # Spring Boot Application

│   ├── src/main/java/

│   │   └── com/carsales/

│   │       ├── controller/           # REST Controllers

│   │       │   ├── CarSalesController.java

│   │       │   ├── AIController.java

│   │       │   └── PdfController.java

│   │       ├── Service/              # Business Logic

│   │       │   ├── CarSalesService.java

│   │       │   ├── CarSalesServiceImpl.java

│   │       │   ├── AIQueryService.java

│   │       │   ├── PdfService.java

│   │       │   └── PdfServiceImpl.java

│   │       ├── repository/           # Data Layer

│   │       │   └── CarSalesRepository.java

│   │       ├── entity/               # JPA Entities

│   │       │   └── CarSales.java

│   │       └── dto/                  # Data Transfer Objects

│   │           ├── YearlyCountDto.java

│   │           ├── MonthlyCountDto.java

│   │           └── UploadSalesResponse.java

│   └── src/main/resources/

│       └── application.yml

│

├── frontend/                         # Python Streamlit App

│   └── app.py                        # Main dashboard file

│

├── screenshots/                      # Project screenshots

└── README.md

```



\---



\## 👨‍💻 Author



\*\*Alok\*\* — Final Year B.Tech CSE Student



> \*"Built SalesRadar to explore the intersection of Java enterprise development and Python data science — proving that the best analytics solutions don't have to choose between backend robustness and data science flexibility."\*



\---



<div align="center">



⭐ \*\*Star this repo if you found it useful!\*\*



!\[Spring Boot](https://img.shields.io/badge/Spring%20Boot%203-6DB33F?style=flat\&logo=spring\&logoColor=white)

!\[Made with ❤️](https://img.shields.io/badge/Made%20with-❤️-red?style=flat)



</div>

