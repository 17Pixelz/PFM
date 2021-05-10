package com.ensas.android.pfm.ui.stats

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ensas.android.pfm.R
import com.ensas.android.pfm.databinding.FragmentStatsBinding
import com.ensas.android.pfm.dbHandler.DbHandler
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.pd.chocobar.ChocoBar


class StatsFragment : Fragment(){

    lateinit var binding: FragmentStatsBinding
    private lateinit var dbHandler: DbHandler


    private lateinit var listPie : ArrayList<PieEntry>
    private lateinit var listColors : ArrayList<Int>


    val vf: ValueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStatsBinding.inflate(inflater)
        dbHandler = DbHandler(context)


        var nchap: Int = dbHandler.getCountQuizs ()

        if(nchap == 0){
            binding.full.visibility = View.GONE
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage("Vous devez au moins repondre a un quiz dans une chapitre pour avoir les statistiques")
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    val nav = findNavController()
                    nav.navigate(R.id.navigation_quiz)
                })
            val alert: AlertDialog = builder.create()
            alert.show()
        }else {
            // Getting number of chapters we have
            nchap = dbHandler.getCountChaps()

            // Filling the fields of our Pie Graph
            listPie = getPieData(nchap)

            // Setting colors for our graph
            listColors = definePieColors()

            // Passing our created pie to our view
            binding.chart.data = setPie()

            // here we putted some configuration to our pieChartView
            configurePie()

            // We add a clickListener to each label of pie labels
            binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e as PieEntry
                    var s = e.label.filter { it.isDigit() }.toInt()
                    val stats = dbHandler.getChapStats(s)
                    ChocoBar.builder().setBackgroundColor(Color.parseColor("#4A74C9"))
                        .setTextSize(16F)
                        .setTextColor(Color.parseColor("#FFFFFF"))
                        .setText("${e.label} \n\tSUCCÈS : ${stats[0]} \n\tÉCHOUER : ${stats[1]}")
                        .setMaxLines(4)
                        .setView(binding.root)
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .build()
                        .show();
                }

                override fun onNothingSelected() {}
            })


            // Here we fill the totals which we have in the top of our view
            binding.ttl.text = dbHandler.getCountQuizs().toString()
            binding.ttlCor.text = dbHandler.getCorCountQuizs().toString()
            binding.ttlWr.text = dbHandler.getWroCountQuizs().toString()

        }
            return binding.root

    }

    private fun configurePie() {

        // Configuration for the center text
        binding.chart.centerText = "Nombre de quiz passee pour chaque chapitre"
        binding.chart.setCenterTextSize(20F)
        binding.chart.setDrawEntryLabels(false)

        // Configuration for the legends
        binding.chart.legend.isEnabled = true
        binding.chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        binding.chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        binding.chart.legend.isWordWrapEnabled = true
        binding.chart.legend.textSize = 14F

        // Configuration for the pie
        binding.chart.setUsePercentValues(false)
        binding.chart.isDrawHoleEnabled = true
        binding.chart.description.isEnabled = false

        /// Configuration for the animation
        binding.chart.animateY(1200, Easing.EaseInOutQuad)
    }

    private fun setPie() : PieData {
        val pieDataSet = PieDataSet(listPie, "")
        pieDataSet.colors = listColors
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(18F)
        pieData.setValueFormatter(vf)

        return pieData
    }

    private fun getPieData(nchap: Int): ArrayList<PieEntry> {
        var pieData = ArrayList<PieEntry>()
        val chaps: ArrayList<Int> = dbHandler.getChaStat()

        for (i in 0 until nchap ) {
            if (chaps[i] != 0) {
                pieData.add(PieEntry(chaps[i].toFloat(), "Chapitre ${i + 1}"))
            }
        }
        return pieData
    }

    fun definePieColors() : ArrayList<Int>{
        var listColors = ArrayList<Int>()
        listColors.add(Color.parseColor("#12B860"))
        listColors.add(Color.parseColor("#5E60CE"))
        listColors.add(Color.parseColor("#48BFE3"))
        listColors.add(Color.parseColor("#72EFDD"))
        listColors.add(Color.parseColor("#E3DB49"))
        listColors.add(Color.parseColor("#E35F85"))
        listColors.add(Color.parseColor("#F4A261"))
        listColors.add(Color.parseColor("#9B2226"))
        listColors.add(Color.parseColor("#CDB4DB"))

        return listColors
    }
}