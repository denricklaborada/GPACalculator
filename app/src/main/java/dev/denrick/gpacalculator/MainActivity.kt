package dev.denrick.gpacalculator

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import dev.denrick.gpacalculator.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException

class MainActivity : AppCompatActivity() {
    private var pref: SharedPreferences? = null
    private var doubleBackToExitPressedOnce = false
    private var courseList = ArrayList<EditText>()
    private var gradeList = ArrayList<EditText>()
    private var unitsList = ArrayList<EditText>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        binding.addBtn.setOnClickListener { addCourse() }
        binding.delBtn.setOnClickListener { deleteCourse() }
        binding.calculateBtn.setOnClickListener { calculateGPA() }

        val count = pref?.getInt("count", -1) ?: -1
        if (count != -1) {
            for (i in 0 until count) {
                val course = pref?.getString("course$i", "") ?: ""
                val grade = pref?.getFloat("grade$i", -1f) ?: -1f
                val units = pref?.getInt("units$i", -1) ?: -1
                addCourse(course, grade, units)
            }
            val form = DecimalFormat("0.000")
            form.roundingMode = RoundingMode.HALF_UP
            try {
                val gpaVal = pref?.getFloat("gpa", 0.000f)
                binding.gpaLbl.text = form.format(form.parse((gpaVal).toString()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            addCourse()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    public override fun onPause() {
        super.onPause()
        saveCurrentState()
    }

    public override fun onStop() {
        super.onStop()
        saveCurrentState()
    }

    private fun saveCurrentState() {
        val editor = pref?.edit()
        editor?.clear()
        val count = gradeList.size
        editor?.putInt("count", count)
        for (i in 0 until count) {
            editor?.putString("course$i", courseList[i].text.toString())
            try {
                editor?.putFloat("grade$i", gradeList[i].text.toString().toFloat())
            } catch (e: NumberFormatException) {
                editor?.putFloat("grade$i", -1f)
            }
            try {
                editor?.putInt("units$i", unitsList[i].text.toString().toInt())
            } catch (e: NumberFormatException) {
                editor?.putInt("units$i", -1)
            }
        }
        editor?.putFloat("gpa", binding.gpaLbl.text.toString().toFloat())
        editor?.apply()
    }

    private fun addCourse() {
        val tl = binding.table
        val count = binding.numCoursesLbl
        val tr = TableRow(this)
        tr.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        val course = EditText(this)
        course.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.0f)
        course.hint = "COURSE" + (courseList.size + 1)
        course.isSingleLine = true
        tr.addView(course)
        courseList.add(course)
        val grade = EditText(this)
        grade.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        grade.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)
        grade.setHint(R.string.gradehint)
        grade.isSingleLine = true
        tr.addView(grade)
        gradeList.add(grade)
        val units = EditText(this)
        units.inputType = InputType.TYPE_CLASS_NUMBER
        units.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)
        units.setHint(R.string.unitshint)
        units.isSingleLine = true
        tr.addView(units)
        unitsList.add(units)
        tl.addView(tr)
        count.text = courseList.size.toString()
        if (count.text.toString().toInt() > 1) {
            binding.delBtn.setText(R.string.delete)
        } else {
            binding.delBtn.setText(R.string.clear)
        }
    }

    private fun addCourse(courseText: String?, gradeText: Float, unitsText: Int) {
        val tl = binding.table
        val count  =binding.numCoursesLbl
        val tr = TableRow(this)
        tr.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        val course = EditText(this)
        course.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.0f)
        course.hint = "COURSE" + (courseList.size + 1)
        course.setText(courseText)
        course.isSingleLine = true
        tr.addView(course)
        courseList.add(course)
        val grade = EditText(this)
        grade.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        grade.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)
        grade.setHint(R.string.gradehint)
        grade.isSingleLine = true
        if (gradeText == -1f) {
            grade.setText("")
        } else {
            grade.setText(gradeText.toString())
        }
        tr.addView(grade)
        gradeList.add(grade)
        val units = EditText(this)
        units.inputType = InputType.TYPE_CLASS_NUMBER
        units.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)
        units.setHint(R.string.unitshint)
        units.isSingleLine = true
        if (unitsText == -1) {
            units.setText("")
        } else {
            units.setText(unitsText.toString())
        }
        tr.addView(units)
        unitsList.add(units)
        tl.addView(tr)
        count.text = courseList.size.toString()
        if (count.text.toString().toInt() > 1) {
            binding.delBtn.setText(R.string.delete)
        } else {
            binding.delBtn.setText(R.string.clear)
        }
    }

    private fun deleteCourse() {
        if (courseList.size > 1) {
            val tl = binding.table
            val count = binding.numCoursesLbl
            tl.removeView(tl.getChildAt(tl.childCount - 1))
            courseList.removeAt(courseList.size - 1)
            gradeList.removeAt(gradeList.size - 1)
            unitsList.removeAt(unitsList.size - 1)
            count.text = courseList.size.toString()
            if (courseList.size == 1) {
                binding.delBtn.setText(R.string.clear)
            }
        } else {
            courseList[0].setText("")
            gradeList[0].setText("")
            unitsList[0].setText("")
            binding.gpaLbl.setText(R.string.gpa)
        }
    }

    private fun calculateGPA() {
        var unitSum = 0
        var product = 0.0f
        val gradeListIterator: Iterator<*> = gradeList.iterator()
        val unitsListIterator: Iterator<*> = unitsList.iterator()
        while (gradeListIterator.hasNext() && unitsListIterator.hasNext()) {
            try {
                val unitTemp = (unitsListIterator.next() as EditText).text.toString().toInt()
                unitSum += unitTemp
                product += unitTemp.toFloat() * (gradeListIterator.next() as EditText).text.toString().toFloat()
            } catch (e: NumberFormatException) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("Please fill out all required input fields.").setCancelable(false).setPositiveButton("OK", null)
                builder.create().show()
                return
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        val gpa = product / unitSum.toFloat()
        val form = DecimalFormat("0.000")
        form.roundingMode = RoundingMode.HALF_UP
        try {
            binding.gpaLbl.text = form.format(form.parse(gpa.toString()))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val PREF_NAME = "MyPref"
    }
}