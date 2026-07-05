# =========================================================
# IMPORT LIBRARIES
# =========================================================

import streamlit as st
import requests
import pandas as pd
import plotly.express as px
import numpy as np
from sklearn.linear_model import LinearRegression


# =========================================================
# PAGE CONFIG
# =========================================================

st.set_page_config(
    page_title="Car Sales Dashboard",
    page_icon="🚗",
    layout="wide"
)

# =========================================================
# CUSTOM CSS
# =========================================================

st.markdown("""
<style>
.main { background-color: #0E1117; }
.stMetric {
    background-color: #1E1E1E;
    padding: 15px;
    border-radius: 10px;
    text-align: center;
}
h1,h2,h3 { color: white; }
.upload-box {
    border: 2px dashed #4CAF50;
    border-radius: 10px;
    padding: 20px;
    text-align: center;
}
</style>
""", unsafe_allow_html=True)


# =========================================================
# TITLE
# =========================================================

st.title("🚗 Car Sales Analytics Dashboard")
st.markdown("### 📊 Real-time Analytics using Spring Boot + Streamlit")

BASE_URL = "http://localhost:8080/api"


# =========================================================
# API FUNCTIONS
# =========================================================

@st.cache_data
def get_yearly_sales():
    try:
        response = requests.get(f"{BASE_URL}/car-sales/yearly-count", timeout=10)
        if response.status_code == 200:
            return pd.DataFrame(response.json()['data'])
    except requests.exceptions.ConnectionError:
        st.error("❌ Cannot connect to Spring Boot server.")
    return pd.DataFrame()


@st.cache_data
def get_monthly_sales(year):
    try:
        response = requests.get(
            f"{BASE_URL}/car-sales/monthly-count",
            params={"year": year}, timeout=10
        )
        if response.status_code == 200:
            return pd.DataFrame(response.json()['data'])
    except requests.exceptions.ConnectionError:
        st.error("❌ Cannot connect to Spring Boot server.")
    return pd.DataFrame()


def ask_ai(question):
    try:
        response = requests.post(f"{BASE_URL}/ai/ask", json=question, timeout=30)
        return response.text
    except requests.exceptions.ConnectionError:
        return "❌ AI service unavailable."


def upload_csv(file):
    try:
        clear_response = requests.delete(f"{BASE_URL}/car-sales/clear", timeout=10)
        if clear_response.status_code != 200:
            st.warning("⚠️ Could not clear previous data.")
        files = {"file": (file.name, file.getvalue(), "text/csv")}
        response = requests.post(f"{BASE_URL}/car-sales/upload-csv", files=files, timeout=30)
        return response
    except requests.exceptions.ConnectionError:
        return None


def download_pdf_report():
    try:
        response = requests.get(f"{BASE_URL}/report/download", timeout=30)
        if response.status_code == 200:
            return response.content
        return None
    except requests.exceptions.ConnectionError:
        return None


@st.cache_data
def get_brand_sales():
    try:
        response = requests.get(f"{BASE_URL}/car-sales/brand-count", timeout=10)
        if response.status_code == 200:
            return pd.DataFrame(response.json()['data'])
    except requests.exceptions.ConnectionError:
        st.error("❌ Cannot connect to Spring Boot server.")
    return pd.DataFrame()


@st.cache_data
def get_color_sales():
    try:
        response = requests.get(f"{BASE_URL}/car-sales/color-count", timeout=10)
        if response.status_code == 200:
            return pd.DataFrame(response.json()['data'])
    except requests.exceptions.ConnectionError:
        st.error("❌ Cannot connect to Spring Boot server.")
    return pd.DataFrame()


@st.cache_data
def get_state_sales():
    try:
        response = requests.get(f"{BASE_URL}/car-sales/state-count", timeout=10)
        if response.status_code == 200:
            return pd.DataFrame(response.json()['data'])
    except requests.exceptions.ConnectionError:
        st.error("❌ Cannot connect to Spring Boot server.")
    return pd.DataFrame()


@st.cache_data
def get_kpi_summary():
    try:
        response = requests.get(f"{BASE_URL}/car-sales/kpi-summary", timeout=10)
        if response.status_code == 200:
            return response.json()['data']
    except requests.exceptions.ConnectionError:
        st.error("❌ Cannot connect to Spring Boot server.")
    return {}


# =========================================================
# SIDEBAR
# =========================================================

st.sidebar.title("📌 Navigation")

option = st.sidebar.radio(
    "Select Module",
    [
        "📤 Upload Data",
        "🎯 KPI Summary",
        "📈 Yearly Analytics",
        "📅 Monthly Analytics",
        "📊 Deep Analytics",
        "🔮 Sales Prediction",
        "🤖 AI Insights"
    ]
)


# =========================================================
# UPLOAD DATA
# =========================================================

if option == "📤 Upload Data":

    st.subheader("📤 Upload Car Sales CSV")
    st.info("ℹ️ Previous data will be cleared automatically before uploading new data.")

    with st.expander("📋 Expected CSV Format (click to expand)"):
        sample_df = pd.DataFrame({
            "year": [2022, 2022, 2023],
            "month": [1, 2, 1],
            "car_model": ["Swift", "Baleno", "Nexon"],
            "sales_count": [120, 95, 150]
        })
        st.dataframe(sample_df, use_container_width=True)

    uploaded_file = st.file_uploader("Choose a CSV file", type=["csv"])

    if uploaded_file is not None:
        st.markdown("### 👀 Preview (first 5 rows)")
        try:
            preview_df = pd.read_csv(uploaded_file)
            st.dataframe(preview_df.head(), use_container_width=True)
            st.caption(f"📊 Total rows: {len(preview_df)} | Columns: {list(preview_df.columns)}")
            uploaded_file.seek(0)
        except Exception as e:
            st.warning(f"Could not preview: {e}")
            uploaded_file.seek(0)

        st.markdown("---")
        col1, col2, col3 = st.columns([1, 1, 1])
        with col2:
            upload_btn = st.button("🚀 Upload to Database", use_container_width=True)

        if upload_btn:
            with st.spinner("⏳ Clearing old data & uploading..."):
                response = upload_csv(uploaded_file)

            if response is None:
                st.error("❌ Upload failed. Cannot reach Spring Boot server.")
            elif response.status_code == 200:
                st.success("✅ File uploaded! Previous data replaced.")
                try:
                    res_data = response.json()
                    col1, col2, col3 = st.columns(3)
                    col1.metric("📦 Total Records", res_data.get('data', {}).get('totalRecords', 'N/A'))
                    col2.metric("✅ Success Count", res_data.get('data', {}).get('successCount', 'N/A'))
                    col3.metric("❌ Failed Count",  res_data.get('data', {}).get('failedCount',  'N/A'))
                except Exception:
                    st.write(response.text)
                st.cache_data.clear()
                st.info("💡 Cache cleared! Check all Analytics sections.")
            else:
                st.error(f"❌ Upload failed: {response.status_code}")
                st.code(response.text)


# =========================================================
# KPI SUMMARY
# =========================================================

elif option == "🎯 KPI Summary":

    st.subheader("🎯 Key Performance Indicators")
    st.markdown("whole business summary in one glance")
    st.markdown("---")

    with st.spinner("Loading KPI Data..."):
        kpi = get_kpi_summary()

    if not kpi:
        st.warning("⚠️ No data found. Upload a CSV first.")
    else:
        col1, col2, col3 = st.columns(3)
        col1.metric("🚗 Total Cars Sold",    kpi.get('totalCars', 'N/A'))
        col2.metric("🏆 Top Selling Brand",  kpi.get('topBrand',  'N/A'))
        col3.metric("🎨 Most Popular Color", kpi.get('topColor',  'N/A'))

        st.markdown("")

        col4, col5, col6 = st.columns(3)
        col4.metric("🗺️ Top State",          kpi.get('topState',   'N/A'))
        col5.metric("⛽ Top Fuel Type",       kpi.get('topFuel',    'N/A'))
        col6.metric("💳 Top Payment Mode",   kpi.get('topPayment', 'N/A'))

        st.markdown("")

        col7, col8, col9 = st.columns(3)
        avg_price = kpi.get('avgPrice', 0)
        col7.metric("💰 Avg Car Price", f"₹{avg_price:,}")
        col8.metric("🏙️ Top City",      kpi.get('topCity', 'N/A'))
        col9.metric("📊 Data Points",   kpi.get('totalCars', 'N/A'))

        st.markdown("---")
        st.markdown("### 📊 Quick Visual Summary")

        col1, col2, col3 = st.columns(3)
        with col1:
            brand_df = get_brand_sales()
            if not brand_df.empty:
                fig = px.pie(brand_df.head(5), names="label", values="count",
                             title="Top 5 Brands", hole=0.5)
                fig.update_layout(template="plotly_dark", title_x=0.5, height=300)
                st.plotly_chart(fig, use_container_width=True)

        with col2:
            color_df = get_color_sales()
            if not color_df.empty:
                fig = px.pie(color_df.head(5), names="label", values="count",
                             title="Top 5 Colors", hole=0.5,
                             color_discrete_sequence=px.colors.qualitative.Set3)
                fig.update_layout(template="plotly_dark", title_x=0.5, height=300)
                st.plotly_chart(fig, use_container_width=True)

        with col3:
            state_df = get_state_sales()
            if not state_df.empty:
                fig = px.bar(state_df.head(5), x="label", y="count",
                             text="count", title="Top 5 States", color="count")
                fig.update_layout(template="plotly_dark", title_x=0.5,
                                  height=300, showlegend=False)
                st.plotly_chart(fig, use_container_width=True)


# =========================================================
# YEARLY ANALYTICS
# =========================================================

elif option == "📈 Yearly Analytics":

    st.subheader("📈 Yearly Car Sales Analysis")

    with st.spinner("Loading Data..."):
        df = get_yearly_sales()

    if df.empty:
        st.warning("⚠️ No Data. Upload CSV first.")
    else:
        col1, col2, col3 = st.columns(3)
        col1.metric("🚗 Total Sales",   df['count'].sum())
        col2.metric("📈 Max Sales",     df['count'].max())
        col3.metric("📊 Average Sales", round(df['count'].mean(), 2))

        st.markdown("---")

        fig = px.line(df, x="year", y="count", markers=True,
                      text="count", title="Yearly Sales Trend")
        fig.update_layout(template="plotly_dark", title_x=0.5)
        st.plotly_chart(fig, use_container_width=True)

        fig2 = px.bar(df, x="year", y="count", text="count",
                      color="count", title="Year-wise Sales")
        fig2.update_layout(template="plotly_dark", title_x=0.5)
        st.plotly_chart(fig2, use_container_width=True)

        st.dataframe(df, use_container_width=True)

        st.markdown("---")
        st.markdown("### 📄 Download Report")
        col1, col2, col3 = st.columns([1, 1, 1])
        with col2:
            if st.button("📄 Generate PDF Report", use_container_width=True):
                with st.spinner("⏳ Generating PDF..."):
                    pdf_data = download_pdf_report()
                if pdf_data:
                    st.download_button(
                        label="⬇️ Click to Save PDF",
                        data=pdf_data,
                        file_name="car_sales_report.pdf",
                        mime="application/pdf",
                        use_container_width=True
                    )
                    st.success("✅ PDF ready!")
                else:
                    st.error("❌ Could not generate PDF.")


# =========================================================
# MONTHLY ANALYTICS
# =========================================================

elif option == "📅 Monthly Analytics":

    st.subheader("📅 Monthly Sales Analysis")

    year = st.number_input("Select Year", min_value=2000, max_value=2100, value=2024)

    with st.spinner("Loading Monthly Data..."):
        df = get_monthly_sales(year)

    if df.empty:
        st.warning("⚠️ No Data Found. Upload CSV first.")
    else:
        month_map = {
            1:"Jan", 2:"Feb", 3:"Mar", 4:"Apr",
            5:"May", 6:"Jun", 7:"Jul", 8:"Aug",
            9:"Sep", 10:"Oct", 11:"Nov", 12:"Dec"
        }
        df["month_name"] = df["month"].map(month_map)

        st.metric("🚗 Total Sales", df["count"].sum())
        st.markdown("---")

        fig = px.bar(df, x="month_name", y="count", text="count",
                     color="count", title=f"Monthly Sales - {year}")
        fig.update_layout(template="plotly_dark", title_x=0.5)
        st.plotly_chart(fig, use_container_width=True)

        fig2 = px.line(df, x="month_name", y="count", markers=True,
                       text="count", title=f"Monthly Trend - {year}")
        fig2.update_layout(template="plotly_dark", title_x=0.5)
        st.plotly_chart(fig2, use_container_width=True)

        st.dataframe(df, use_container_width=True)


# =========================================================
# DEEP ANALYTICS
# =========================================================

elif option == "📊 Deep Analytics":

    st.subheader("📊 Deep Sales Analytics")
    st.markdown("Brand, Color aur State wise breakdown")
    st.markdown("---")

    st.markdown("### 🏷️ Brand-wise Sales")
    brand_df = get_brand_sales()
    if not brand_df.empty:
        col1, col2 = st.columns(2)
        with col1:
            fig = px.pie(brand_df, names="label", values="count",
                         title="Brand-wise Distribution", hole=0.4)
            fig.update_layout(template="plotly_dark", title_x=0.5)
            st.plotly_chart(fig, use_container_width=True)
        with col2:
            fig2 = px.bar(brand_df, x="label", y="count", text="count",
                          color="count", title="Brand-wise Count")
            fig2.update_layout(template="plotly_dark", title_x=0.5)
            st.plotly_chart(fig2, use_container_width=True)
        top = brand_df.iloc[0]
        st.metric("🏆 Top Selling Brand", top['label'], f"{top['count']} cars")
        st.dataframe(brand_df, use_container_width=True)

    st.markdown("---")

    st.markdown("### 🎨 Color-wise Sales")
    color_df = get_color_sales()
    if not color_df.empty:
        col1, col2 = st.columns(2)
        with col1:
            fig = px.pie(color_df, names="label", values="count",
                         title="Color-wise Distribution", hole=0.4,
                         color_discrete_sequence=px.colors.qualitative.Set3)
            fig.update_layout(template="plotly_dark", title_x=0.5)
            st.plotly_chart(fig, use_container_width=True)
        with col2:
            fig2 = px.bar(color_df, x="label", y="count", text="count",
                          color="label", title="Color-wise Count",
                          color_discrete_sequence=px.colors.qualitative.Set3)
            fig2.update_layout(template="plotly_dark", title_x=0.5)
            st.plotly_chart(fig2, use_container_width=True)
        top = color_df.iloc[0]
        st.metric("🎨 Most Popular Color", top['label'], f"{top['count']} cars")
        st.dataframe(color_df, use_container_width=True)

    st.markdown("---")

    st.markdown("### 🗺️ State-wise Sales")
    state_df = get_state_sales()
    if not state_df.empty:
        col1, col2 = st.columns(2)
        with col1:
            fig = px.pie(state_df, names="label", values="count",
                         title="State-wise Distribution", hole=0.4)
            fig.update_layout(template="plotly_dark", title_x=0.5)
            st.plotly_chart(fig, use_container_width=True)
        with col2:
            fig2 = px.bar(state_df, x="label", y="count", text="count",
                          color="count", title="State-wise Count")
            fig2.update_layout(template="plotly_dark", title_x=0.5)
            st.plotly_chart(fig2, use_container_width=True)
        top = state_df.iloc[0]
        st.metric("🗺️ Top State", top['label'], f"{top['count']} cars")
        st.dataframe(state_df, use_container_width=True)


# =========================================================
# SALES PREDICTION ← NEW
# =========================================================

elif option == "🔮 Sales Prediction":

    st.subheader("🔮 ML-Based Sales Prediction")
    st.markdown(" Predicting future sales forecast from Linear Regression model ")
    st.markdown("---")

    with st.spinner("Loading data for prediction..."):
        df = get_yearly_sales()

    if df.empty or len(df) < 3:
        st.warning("⚠️ minimum 3 year data requred to predict . upload csv first .")
    else:
        # ── TRAIN MODEL ──
        X = df['year'].values.reshape(-1, 1)
        y = df['count'].values

        model = LinearRegression()
        model.fit(X, y)

        # Accuracy score
        r2_score = model.score(X, y)
        accuracy  = round(r2_score * 100, 2)

        # ── PREDICT FUTURE ──
        forecast_years = st.slider(
            "How many years ahead do you want to predict?",
            min_value=1, max_value=5, value=3
        )

        last_year    = int(df['year'].max())
        future_years = list(range(last_year + 1, last_year + forecast_years + 1))
        future_X     = np.array(future_years).reshape(-1, 1)
        predictions  = model.predict(future_X).astype(int)
        predictions  = np.maximum(predictions, 0)  # negative nahi hone denge

        # ── METRICS ──
        st.markdown("### 📊 Model Performance")
        col1, col2, col3 = st.columns(3)
        col1.metric("🎯 Model Accuracy (R²)", f"{accuracy}%")
        col2.metric("📅 Training Data",        f"{len(df)} years")
        col3.metric("🔮 Predicting",           f"{forecast_years} years ahead")

        st.markdown("---")

        # ── COMBINED CHART (Actual + Predicted) ──
        actual_df = pd.DataFrame({
            'year':  df['year'].tolist(),
            'count': df['count'].tolist(),
            'type':  ['Actual'] * len(df)
        })

        predicted_df = pd.DataFrame({
            'year':  future_years,
            'count': predictions.tolist(),
            'type':  ['Predicted'] * len(future_years)
        })

        combined_df = pd.concat([actual_df, predicted_df], ignore_index=True)

        fig = px.line(
            combined_df,
            x='year', y='count',
            color='type',
            markers=True,
            text='count',
            title="📈 Actual vs Predicted Sales",
            color_discrete_map={
                'Actual':    '#3498DB',
                'Predicted': '#E74C3C'
            }
        )
        fig.update_traces(textposition="top center")
        fig.update_layout(
            template="plotly_dark",
            title_x=0.5,
            xaxis_title="Year",
            yaxis_title="Sales Count",
            legend_title="Type"
        )
        st.plotly_chart(fig, use_container_width=True)

        # ── PREDICTED TABLE ──
        st.markdown("### 🔮 Predicted Sales Table")
        pred_table = pd.DataFrame({
            'Year':            future_years,
            'Predicted Sales': predictions.tolist()
        })
        st.dataframe(pred_table, use_container_width=True)

        # ── INSIGHT ──
        st.markdown("---")
        st.markdown("### 💡 AI Insight")
        best_pred_year  = future_years[predictions.argmax()]
        best_pred_count = predictions.max()
        trend = "📈 Upward" if predictions[-1] > predictions[0] else "📉 Downward"

        col1, col2 = st.columns(2)
        col1.metric("🏆 Best Predicted Year",  str(best_pred_year))
        col2.metric("📊 Predicted Trend",      trend)

        st.info(f"""
        🤖 **Model says:**
        - Predicted best year: **{best_pred_year}** with **{best_pred_count}** cars
        - Overall trend: **{trend}**
        - Model R² Score: **{accuracy}%** (higher = better fit)
        """)

        st.caption("⚠️ Note: Linear Regression is a simple model. Real-world predictions depend on many more factors.")


# =========================================================
# AI INSIGHTS
# =========================================================

elif option == "🤖 AI Insights":

    st.subheader("🤖 AI Powered Car Sales Insights")

    question = st.text_area(
        "Ask Anything",
        placeholder="Example: Which year had highest sales?"
    )

    if st.button("Ask AI"):
        if question.strip() == "":
            st.warning("Please enter a question")
        else:
            with st.spinner("AI Thinking..."):
                answer = ask_ai(question)
            st.success("AI Response")
            st.write(answer)


# =========================================================
# FOOTER
# =========================================================

st.markdown("---")
st.caption("🚀 Built with Streamlit + Spring Boot + Spring AI + scikit-learn + Plotly | By ALOK")