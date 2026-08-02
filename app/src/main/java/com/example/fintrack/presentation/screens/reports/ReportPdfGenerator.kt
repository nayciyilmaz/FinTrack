package com.example.fintrack.presentation.screens.reports

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.pdf.PdfDocument
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.example.fintrack.R
import com.example.fintrack.core.constants.categoryKeyToLabelResId
import com.example.fintrack.domain.model.Budget
import com.example.fintrack.domain.model.SavingsGoal
import com.example.fintrack.domain.model.Transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object ReportPdfGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    fun generate(
        context: Context,
        periodLabel: String,
        income: Int,
        expense: Int,
        transactions: List<Transaction>,
        categoryDistribution: List<ReportCategoryItem>,
        spendingTrend: List<ReportTrendItem>,
        trendHigh: ReportTrendSummary?,
        trendLow: ReportTrendSummary?,
        budgets: List<Budget>,
        categoryExpenses: Map<String, Int>,
        savingsGoals: List<SavingsGoal>,
        savingsEstimatedDates: Map<Long, String?>,
        selectedSections: Set<Int>
    ): Boolean {
        return try {
            val document = PdfDocument()
            val writer = PdfWriter(context, document)

            writer.drawHeader(periodLabel, income, expense)

            if (selectedSections.contains(R.string.report_content_transaction_summary)) {
                writer.drawTransactionSummary(transactions)
            }
            if (selectedSections.contains(R.string.report_content_category_analysis)) {
                writer.drawCategoryAnalysis(categoryDistribution)
            }
            if (selectedSections.contains(R.string.report_content_spending_trend)) {
                writer.drawSpendingTrend(spendingTrend, trendHigh, trendLow)
            }
            if (selectedSections.contains(R.string.title_budget_limits)) {
                writer.drawBudgetLimits(budgets, categoryExpenses)
            }
            if (selectedSections.contains(R.string.title_savings_goals)) {
                writer.drawSavingsGoals(savingsGoals, savingsEstimatedDates)
            }

            writer.finish()

            val fileName = "Rapor_${periodLabel.replace(" ", "_")}.pdf"
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return false
            resolver.openOutputStream(uri)?.use { output -> document.writeTo(output) }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            document.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private class PdfWriter(
        private val context: Context,
        private val document: PdfDocument
    ) {
        private val locale = Locale("tr")
        private val contentWidth = PAGE_WIDTH - 2 * MARGIN
        private val chartColors = context.resources.getIntArray(R.array.chart_colors)

        private val titlePaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.text_primary)
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        private val subtitlePaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.bottom_bar_fab)
            textSize = 12f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        private val sectionPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.text_primary)
            textSize = 13f
            isFakeBoldText = true
            isAntiAlias = true
        }
        private val bodyPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.text_primary)
            textSize = 10.5f
            isAntiAlias = true
        }
        private val mutedPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.text_secondary)
            textSize = 9f
            isAntiAlias = true
        }
        private val mutedRightPaint = Paint(mutedPaint).apply { textAlign = Paint.Align.RIGHT }
        private val incomePaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.income_green)
            textSize = 10.5f
            isAntiAlias = true
        }
        private val expensePaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.expense_red)
            textSize = 10.5f
            isAntiAlias = true
        }
        private val incomeRightPaint = Paint(incomePaint).apply { textAlign = Paint.Align.RIGHT }
        private val expenseRightPaint = Paint(expensePaint).apply { textAlign = Paint.Align.RIGHT }
        private val headerIncomePaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.income_green)
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }
        private val headerExpensePaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.expense_red)
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }
        private val trackPaint = Paint().apply { color = Color.parseColor("#F1EFE8") }
        private val linePaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.bottom_bar_fab)
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        private val pointPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.bottom_bar_fab)
            isAntiAlias = true
        }
        private val dividerPaint = Paint().apply {
            color = Color.parseColor("#D3D1C7")
            strokeWidth = 0.5f
        }

        private var pageNumber = 0
        private var page: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var y = 0f

        private fun startPage() {
            pageNumber++
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            val newPage = document.startPage(pageInfo)
            page = newPage
            canvas = newPage.canvas
            y = MARGIN
        }

        private fun closePage() {
            page?.let { document.finishPage(it) }
            page = null
            canvas = null
        }

        private fun requireSpace(height: Float) {
            if (page == null) {
                startPage()
                return
            }
            if (y + height > PAGE_HEIGHT - MARGIN) {
                closePage()
                startPage()
            }
        }

        fun finish() {
            closePage()
        }

        private fun formatMoney(amount: Number): String {
            return "%,d".format(amount.toInt()).replace(",", ".")
        }

        fun drawHeader(periodLabel: String, income: Int, expense: Int) {
            requireSpace(90f)
            val c = canvas!!
            c.drawText(context.getString(R.string.report_title), PAGE_WIDTH / 2f, y + 16f, titlePaint)
            c.drawText("$periodLabel dönemi", PAGE_WIDTH / 2f, y + 34f, subtitlePaint)
            y += 48f

            val boxWidth = (contentWidth - 12f) / 2f
            c.drawText(context.getString(R.string.report_total_income), MARGIN + 8f, y + 14f, mutedPaint)
            c.drawText("₺${formatMoney(income)}", MARGIN + 8f, y + 32f, headerIncomePaint)
            c.drawText(context.getString(R.string.report_total_expense), MARGIN + boxWidth + 20f, y + 14f, mutedPaint)
            c.drawText("₺${formatMoney(expense)}", MARGIN + boxWidth + 20f, y + 32f, headerExpensePaint)
            y += 46f
        }

        private fun drawSectionTitle(title: String) {
            requireSpace(24f)
            val c = canvas!!
            c.drawText(title, MARGIN, y + 12f, sectionPaint)
            y += 16f
            c.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, dividerPaint)
            y += 12f
        }

        fun drawTransactionSummary(transactions: List<Transaction>) {
            drawSectionTitle(context.getString(R.string.report_content_transaction_summary))
            if (transactions.isEmpty()) {
                drawEmptyLine()
                return
            }
            val dateFormatter = DateTimeFormatter.ofPattern("d MMM", locale)
            transactions.sortedBy { it.date }.forEach { transaction ->
                requireSpace(16f)
                val c = canvas!!
                val date = LocalDate.parse(transaction.date).format(dateFormatter)
                val categoryLabel = context.getString(categoryKeyToLabelResId(transaction.category))
                val isIncome = transaction.type == "INCOME"
                val amountText = "${if (isIncome) "+" else "-"}₺${formatMoney(transaction.amount)}"
                c.drawText(date, MARGIN, y + 10f, mutedPaint)
                c.drawText(categoryLabel, MARGIN + 60f, y + 10f, bodyPaint)
                val amountPaint = if (isIncome) incomeRightPaint else expenseRightPaint
                c.drawText(amountText, PAGE_WIDTH - MARGIN, y + 10f, amountPaint)
                y += 16f
            }
            y += 8f
        }

        fun drawCategoryAnalysis(items: List<ReportCategoryItem>) {
            drawSectionTitle(context.getString(R.string.report_content_category_analysis))
            if (items.isEmpty()) {
                drawEmptyLine()
                return
            }
            items.forEachIndexed { index, item ->
                requireSpace(30f)
                val c = canvas!!
                val label = context.getString(categoryKeyToLabelResId(item.categoryKey))
                c.drawText(label, MARGIN, y + 10f, bodyPaint)
                c.drawText("%${item.percentage.toInt()} · ₺${formatMoney(item.amount)}", PAGE_WIDTH - MARGIN, y + 10f, mutedRightPaint)
                y += 14f
                drawBar(item.percentage / 100f, chartColors[index % chartColors.size])
                y += 12f
            }
            y += 4f
        }

        fun drawSpendingTrend(items: List<ReportTrendItem>, high: ReportTrendSummary?, low: ReportTrendSummary?) {
            drawSectionTitle(context.getString(R.string.report_content_spending_trend))
            if (items.isEmpty() || items.all { it.amount == 0f }) {
                drawEmptyLine()
                return
            }
            requireSpace(90f)
            val c = canvas!!
            val chartHeight = 60f
            val chartTop = y
            val chartBottom = chartTop + chartHeight
            val maxAmount = items.maxOf { it.amount }.coerceAtLeast(1f)
            val stepX = if (items.size > 1) contentWidth / (items.size - 1) else 0f

            val path = Path()
            items.forEachIndexed { index, item ->
                val px = MARGIN + stepX * index
                val py = chartBottom - (item.amount / maxAmount) * chartHeight
                if (index == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            c.drawPath(path, linePaint)
            items.forEachIndexed { index, item ->
                val px = MARGIN + stepX * index
                val py = chartBottom - (item.amount / maxAmount) * chartHeight
                c.drawCircle(px, py, 2.5f, pointPaint)
            }
            y = chartBottom + 14f

            requireSpace(20f)
            val c2 = canvas!!
            if (high != null) c2.drawText("En çok: ${high.date} · ₺${formatMoney(high.amount)}", MARGIN, y + 10f, mutedPaint)
            if (low != null) c2.drawText("En az: ${low.date} · ₺${formatMoney(low.amount)}", PAGE_WIDTH - MARGIN, y + 10f, mutedRightPaint)
            y += 20f
        }

        fun drawBudgetLimits(budgets: List<Budget>, categoryExpenses: Map<String, Int>) {
            drawSectionTitle(context.getString(R.string.title_budget_limits))
            if (budgets.isEmpty()) {
                drawEmptyLine()
                return
            }
            budgets.forEach { budget ->
                requireSpace(36f)
                val c = canvas!!
                val used = categoryExpenses[budget.category] ?: 0
                val limit = budget.limitAmount.toInt().coerceAtLeast(1)
                val progress = (used.toFloat() / limit.toFloat()).coerceIn(0f, 1f)
                val percentage = (progress * 100).toInt()
                val remaining = (limit - used).coerceAtLeast(0)
                val label = context.getString(categoryKeyToLabelResId(budget.category))
                val color = when {
                    used >= limit -> ContextCompat.getColor(context, R.color.expense_red)
                    progress >= 0.75f -> ContextCompat.getColor(context, R.color.bottom_bar_fab)
                    else -> ContextCompat.getColor(context, R.color.income_green)
                }
                c.drawText(label, MARGIN, y + 10f, bodyPaint)
                c.drawText("₺${formatMoney(used)} / ₺${formatMoney(limit)}", PAGE_WIDTH - MARGIN, y + 10f, mutedRightPaint)
                y += 14f
                drawBar(progress, color)
                y += 12f
                c.drawText("%$percentage kullanıldı · Kalan ₺${formatMoney(remaining)}", MARGIN, y + 8f, mutedPaint)
                y += 16f
            }
            y += 4f
        }

        fun drawSavingsGoals(goals: List<SavingsGoal>, estimatedDates: Map<Long, String?>) {
            drawSectionTitle(context.getString(R.string.title_savings_goals))
            if (goals.isEmpty()) {
                drawEmptyLine()
                return
            }
            goals.forEach { goal ->
                requireSpace(36f)
                val c = canvas!!
                val progress = (goal.currentAmount / goal.targetAmount.coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f)
                val percentage = (progress * 100).toInt()
                c.drawText("${goal.name} (${goal.category})", MARGIN, y + 10f, bodyPaint)
                c.drawText("₺${formatMoney(goal.currentAmount)} / ₺${formatMoney(goal.targetAmount)}", PAGE_WIDTH - MARGIN, y + 10f, mutedRightPaint)
                y += 14f
                drawBar(progress, ContextCompat.getColor(context, R.color.bottom_bar_fab))
                y += 12f
                val estimatedDate = estimatedDates[goal.id]
                val statusText = if (estimatedDate != null) {
                    "%$percentage tamamlandı · Tahmini bitiş: $estimatedDate"
                } else {
                    "%$percentage tamamlandı"
                }
                c.drawText(statusText, MARGIN, y + 8f, mutedPaint)
                y += 16f
            }
            y += 4f
        }

        private fun drawBar(progress: Float, color: Int) {
            val c = canvas!!
            val barHeight = 5f
            c.drawRoundRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + barHeight, 3f, 3f, trackPaint)
            val fillWidth = contentWidth * progress.coerceIn(0f, 1f)
            if (fillWidth > 0) {
                val fillPaint = Paint().apply {
                    this.color = color
                    isAntiAlias = true
                }
                c.drawRoundRect(MARGIN, y, MARGIN + fillWidth, y + barHeight, 3f, 3f, fillPaint)
            }
        }

        private fun drawEmptyLine() {
            requireSpace(16f)
            canvas!!.drawText("Kayıt yok", MARGIN, y + 10f, mutedPaint)
            y += 20f
        }
    }
}
