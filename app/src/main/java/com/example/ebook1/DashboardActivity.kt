package com.example.ebook1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.ebook1.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        //Создание страницы
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Инициализируем firebase индификатор
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        //Обрабатываем нажатие, выход из аккаунта
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    // функция создает такого "помощника" (адаптер) и настраивает его для листалки
    private fun setupWithViewPagerAdapter(viewPager: ViewPager){
        viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager, // Это менеджер, который управляет экранами
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, // Оптимизация: работает только текущий экран
            this // Это ссылка на текущую активность (экран приложения)
        )

        //init list
        categoryArrayList = ArrayList()

        // Загружаем категории
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Очищаем список
                categoryArrayList.clear()

               // Загрузка статических категорий:
                val modelAll = ModelCategory("01", "All", 1, "")
                val modelMostViewed = ModelCategory("01", "Most Viewed", 1, "")
                val modelMostDownloaded = ModelCategory("01", "Most Downloaded", 1, "")
                //add to list
                categoryArrayList.add(modelAll)
                categoryArrayList.add(modelMostViewed)
                categoryArrayList.add(modelMostDownloaded)

                //Для каждой категории создается экран (BooksUserFragment) и добавляется в адаптер.
                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${modelAll.id}",
                        "${modelAll.category}",
                        "${modelAll.uid}"
                    ),modelAll.category
                )
                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${modelMostViewed.id}",
                        "${modelMostViewed.category}",
                        "${modelMostViewed.uid}"
                    ),modelMostViewed.category
                )
                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${modelMostDownloaded.id}",
                        "${modelMostDownloaded.category}",
                        "${modelMostDownloaded.uid}"
                    ),modelMostDownloaded.category
                )

                //Обновляем адаптер
                viewPagerAdapter.notifyDataSetChanged()

                // Проходимся по категориям в бд и добавляеем их
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add to list
                    categoryArrayList.add(model!!)
                    //add to viewPagerAdapter
                    viewPagerAdapter.addFragment(
                        BooksUserFragment.newInstance(
                            "${model.id}",
                            "${model.category}",
                            "${model.uid}"
                        ),model.category
                    )
                }

                //refresh list
                viewPagerAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        //Адаптер передается в листалку (ViewPager), чтобы она знала, какие экраны показывать.
        viewPager.adapter = viewPagerAdapter

    }

    class ViewPagerAdapter (fm: FragmentManager, behavior:Int, context: Context):FragmentPagerAdapter(fm, behavior){
        // Это список экранов (фрагментов). Каждый экран — это отдельная "страничка", которую можно листать.
        private val fragmentList: ArrayList<BooksUserFragment> = ArrayList()
        //Это список названий для этих экранов. Эти названия будут отображаться на вкладках (если они есть).
        private val fragmentTitleList: ArrayList<String> = ArrayList()

        private val context : Context

        init {
            this.context = context
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        // Возвращает экран (фрагмент) для конкретной позиции.
        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        // Возвращает название для вкладки
        override fun getPageTitle(position: Int): CharSequence{
            return fragmentTitleList[position]
        }

        // функция добавляет новый экран и его название в списки:
        public fun addFragment (fragment: BooksUserFragment, title : String){
            //add fragment that will be passed as parameter in fragmentList
            fragmentList.add(fragment)
            //add title that will be passed as parameter
            fragmentTitleList.add(title)
        }

    }

    private fun checkUser() {
        // получаем текущего пользователя
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            binding.subTitleTv.text = "Not Logged In"
        }else{
            //Пользователь залогинился, получаем и показываем информацию об пользователе
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}