package f1.sleepLeaderimport android.content.Contextimport android.media.MediaPlayerimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundleimport android.os.Environmentimport android.util.Logimport android.widget.RadioButtonimport android.widget.RadioGroupimport android.widget.Toastimport androidx.annotation.BoolResimport com.google.firebase.storage.FirebaseStorageimport com.google.firebase.storage.StorageReferenceimport io.realm.Realmimport io.realm.kotlin.createObjectimport io.realm.kotlin.whereimport kotlinx.android.synthetic.main.activity_alarm_list.*import kotlinx.android.synthetic.main.activity_fade_in.*import kotlinx.android.synthetic.main.activity_music_down_load.*import kotlinx.android.synthetic.main.activity_music_down_load.listViewimport org.jetbrains.anko.startActivityimport java.io.Fileimport java.io.FileOutputStreamimport java.lang.Exceptionopen class MusicDownLoadActivity : MediaPlayerActivity(){    private var musicRealm: Realm = Realm.getDefaultInstance()    private var musicAlarmRealm: Realm = Realm.getDefaultInstance()    private var mList : ArrayList<String> = arrayListOf()    private var aList : ArrayList<String> = arrayListOf()    private var musicNameList : ArrayList<String> = arrayListOf()    private var musicPathList : ArrayList<String> = arrayListOf()    private var flag :Boolean = false    private var musicNameSetList : ArrayList<String> = arrayListOf()    private var musicPathSetList : ArrayList<String> = arrayListOf()    private var insertFlag : Boolean = false    private var insertedFlag : Boolean = false    private var cancel = false    private var index : Int =0    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_music_down_load)        val musicList = musicRealm.where<MusicTable>().findAll()        val musicAlarmList = musicAlarmRealm.where<MusicAlarmTable>().findAll()        for (id in 1..musicList.size) {            val name = musicRealm.where<MusicTable>().equalTo("musicId", id).findFirst()            mList.add(name?.musicName.toString())        }        for (id in 1..musicAlarmList.size) {            val name = musicAlarmRealm.where<MusicAlarmTable>().equalTo("musicAlarmId", id).findFirst()            aList.add(name?.musicAlarmName.toString())        }        sleepbutton.isChecked = true        Log.i("Test","1")        val storage = FirebaseStorage.getInstance()        val storageRef = storage.reference        println(storage)        val dir = storageRef.child("MusicName/MusicName.txt")        val dir2 = storageRef.child("MusicPath/MusicPath.txt")        Log.i("Test","2")        println(dir)        val byteLength=1024*1024L        dir.getBytes(byteLength).addOnSuccessListener{                bytes ->            Log.i("Test","3")            val data = String(bytes, Charsets.UTF_8)            val array=data.split("\n")            Log.i("Test","4")            for(item in array){                musicNameList.add(item)            }            println(musicNameList)            if(musicPathList.size==0) {            }else{                val musicList = List(musicNameList.size) { i ->                    FireBaseData(                        musicNameList[i],                        musicPathList[i]                    )                }                listView.adapter = MusicListAdapter(this, musicList)                flag = true            }            //test.text=res        }.addOnFailureListener {  }        dir2.getBytes(byteLength).addOnSuccessListener{                bytes ->            Log.i("Test","3")            val data = String(bytes, Charsets.UTF_8)            val array=data.split("\n")            var res=""            val i=1;            Log.i("Test","4")            for(item in array){                musicPathList.add(item)            }            println(musicPathList)            if(musicNameList.size==0) {            }else{                val musicList = List(musicNameList.size) { i ->                    FireBaseData(                        musicNameList[i],                        musicPathList[i]                    )                }                listView.adapter = MusicListAdapter(this, musicList)                flag = true            }        }.addOnFailureListener {  }    }    override fun onResume() {        super.onResume()        listView.setOnItemClickListener { parent, view, position, id ->            val musicPostion = parent.getItemAtPosition(position) as FireBaseData            val musicName = musicPostion.musicName            val musicPath = musicPostion.musicPath            if (index == 0) {                for (id in 1..mList.size) {                    val named = mList.get(id - 1)                    if (musicName.equals(named)) {                        insertedFlag = true                    }                }                if (insertedFlag) {                    Toast.makeText(this, "就寝時に既に登録されています", Toast.LENGTH_LONG).show()                    insertedFlag = false                } else {                    selectedMusic(musicName, musicPath)                }            } else if (index == 1) {                for (id in 1..aList.size) {                    val named = aList.get(id - 1)                    if (musicName.equals(named)) {                        insertedFlag = true                    }                }                if (insertedFlag) {                    Toast.makeText(this, "起床時に既に登録されています", Toast.LENGTH_LONG).show()                    insertedFlag = false                } else {                    selectedMusic(musicName, musicPath)                }            }        }        val radioGroup: RadioGroup = findViewById(R.id.selectRadio)        radioGroup.setOnCheckedChangeListener { _, checkedId: Int ->            when (checkedId) {                R.id.sleepbutton -> index = 0                R.id.morningbutton -> index = 1            }        }        insertButton.setOnClickListener {            if (musicNameSetList.size == 0) {                Toast.makeText(this, "何も選択されていません", Toast.LENGTH_LONG).show()            } else {                for (i in 1..musicNameSetList.size) {                    if (index == 0) {                        for (id in 1..mList.size) {                            if (musicNameSetList[i - 1].equals(mList[id - 1])) {                                cancel = true                            }                        }                        if (cancel) {                            cancel = false                        } else {                            musicRealm.executeTransaction {                                var maxId = musicRealm.where<MusicTable>().max("musicId")                                var nextId = (maxId?.toLong() ?: 0L) + 1                                var music = musicRealm.createObject<MusicTable>(nextId)                                music.musicName = musicNameSetList[i - 1]                                music.musicPath = musicPathSetList[i - 1]                                music.firebaseFlag = "ON"                            }                        }                    } else if (index == 1) {                        for (id in 1..aList.size) {                            if (musicNameSetList[i - 1].equals(aList[id - 1])) {                                cancel = true                            }                        }                        if (cancel) {                            cancel = false                        } else {                            musicAlarmRealm.executeTransaction {                                var maxId =                                    musicAlarmRealm.where<MusicAlarmTable>().max("musicAlarmId")                                var nextId = (maxId?.toLong() ?: 0L) + 1                                var music = musicAlarmRealm.createObject<MusicAlarmTable>(nextId)                                music.musicAlarmName = musicNameSetList[i - 1]                                music.musicAlarmPath = musicPathSetList[i - 1]                                music.firebaseAlarmFlag = "ON"                            }                        }                    }                    startActivity<AlarmActivity>()                }            }        }    }    fun selectedMusic(musicName : String , musicPath : String){        var flag : Boolean = true        if(musicName != null){            if(insertFlag) {                for (i in 1..musicNameSetList.size) {                    val name = musicNameSetList.get(i-1)                    if (name.equals(musicName)) {                        flag = false                    }                }                if (flag) {                    musicNameSetList.add(musicName)                    musicPathSetList.add(musicPath)                    Toast.makeText(this, "登録", Toast.LENGTH_LONG).show()                } else {                    musicNameSetList.remove(musicName)                    musicPathSetList.remove(musicPath)                    Toast.makeText(this, "解除", Toast.LENGTH_LONG).show()                }            }else {                musicNameSetList.add(musicName)                musicPathSetList.add(musicPath)                Toast.makeText(this, "登録", Toast.LENGTH_LONG).show()                insertFlag = true            }        }    }}