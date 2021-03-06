package com.app

import com.app.model.*
import com.app.view.*
import javafx.application.*
import javafx.collections.*
import javafx.fxml.*
import javafx.scene.*
import javafx.scene.control.*
import javafx.scene.image.*
import javafx.scene.layout.*
import javafx.stage.*
import java.io.*
import java.util.prefs.*
import javax.xml.bind.*

/**
 * Created by IntelliJ IDEA.<br>
 * User: TrofimovAA<br>
 * Date: 25.03.2017<br>
 * Time: 15:39<br>
 * Основной класс приложения. Является точкой входа для приложения с UI на JavaFX.
 * Здесь же хранится список работников для таблицы.
 */
class MainApp : Application() {
    /**
     * Основной контейнер
     */
    lateinit var primaryStage: Stage

    /**
     * Базовый макет приложения
     */
    private lateinit var rootLayout: BorderPane

    /**
     * Данные таблицы работников
     */
    val workersData = FXCollections.observableArrayList<AbstractWorker>()!!

    companion object {
        @JvmStatic
        fun main(arg: Array<String>) {
            if (arg.isNotEmpty()) {
                filePath = arg[0]
            }
            launch(MainApp::class.java)
        }

        /**
         * Путь к файлу, получаемый при передаче пути в качестве параметра при запуске
         */
        var filePath = ""
    }

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage
        this.primaryStage.icons.add(Image("file:resources/images/handbook.png"))
        if (filePath.isNotEmpty()) {
            loadPersonDataFromFile(File(filePath))
        } else {
            setFilePath(null)
        }
        initRootLayout()
        showWorkersOverview()
    }

    /**
     * Инициализация базового макета
     */
    private fun initRootLayout() {
        try {
            val loader = FXMLLoader()
            loader.location = MainApp.javaClass.getResource("/com/app/view/RootLayout.fxml")
            rootLayout = loader.load<Parent?>() as BorderPane
            primaryStage.scene = Scene(rootLayout)
            loader.getController<RootLayoutController>().mainApp = this
            primaryStage.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Открытие таблицы с работниками
     */
    private fun showWorkersOverview() {
        try {
            val loader = FXMLLoader()
            loader.location = MainApp.javaClass.getResource("/com/app/view/WorkersOverview.fxml")
            val pane = loader.load<Parent?>() as AnchorPane
            val workerDetailsLoader = FXMLLoader()
            workerDetailsLoader.location = MainApp.javaClass.getResource("/com/app/view/WorkerEditDialog.fxml")
            val overViewPane = workerDetailsLoader.load<Parent?>() as AnchorPane
            (pane.children[0] as SplitPane).items[1] = overViewPane
            rootLayout.center = pane
            val workerDetailsController = workerDetailsLoader.getController<WorkerEditDialogController>()
            workerDetailsController.edited = false
            val controller = loader.getController<WorkersOverviewController>()
            controller.mainApp = this
            controller.workerDetailsController = workerDetailsController
            workerDetailsController.workerOverviewController = controller
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Открытие дилога создания работника
     * @return AbstractWorker
     * @see AbstractWorker
     * @see WorkerFactory.create
     */
    fun showWorkerCreateDialog(): AbstractWorker? {
        val loader = FXMLLoader()
        loader.location = MainApp.javaClass.getResource("/com/app/view/WorkerEditDialog.fxml")
        val dialogStage = Stage()
        dialogStage.title = "Добавить работника"
        dialogStage.initModality(Modality.WINDOW_MODAL)
        dialogStage.initOwner(primaryStage)
        dialogStage.scene = Scene(loader.load())
        val controller = loader.getController<WorkerEditDialogController>()
        controller.edited = true
        controller.dialogStage = dialogStage
        dialogStage.showAndWait()
        return controller.createWorker()
    }

    /**
     * Открытие диалога редактирования работника
     * @return AbstractWorker
     * @see AbstractWorker
     */
    fun showWorkerEditDialog(worker: AbstractWorker?): AbstractWorker? {
        val loader = FXMLLoader()
        loader.location = MainApp.javaClass.getResource("/com/app/view/WorkerEditDialog.fxml")
        val dialogStage = Stage()
        dialogStage.title = "Редактировать работника"
        dialogStage.initModality(Modality.WINDOW_MODAL)
        dialogStage.initOwner(primaryStage)
        dialogStage.scene = Scene(loader.load())
        val controller = loader.getController<WorkerEditDialogController>()
        controller.edited = true
        controller.worker = worker
        controller.dialogStage = dialogStage
        dialogStage.showAndWait()
        val newWorker = controller.createWorker()
        if (newWorker != null) {
            val index = workersData.indexOf(worker)
            workersData.remove(worker)
            workersData.add(index, newWorker)
            return newWorker
        }
        return worker
    }

    /**
     * Открытие окна формы поиска
     */
    fun showSearchForm() {
        val loader = FXMLLoader()
        loader.location = MainApp.javaClass.getResource("/com/app/view/SearchForm.fxml")
        val dialogStage = Stage()
        dialogStage.title = "Поиск"
        dialogStage.initModality(Modality.WINDOW_MODAL)
        dialogStage.initOwner(primaryStage)
        dialogStage.scene = Scene(loader.load())
        val controller = loader.getController<SearchFormController>()
        controller.mainApp = this
        loader.getController<SearchFormController>().dialogStage = dialogStage
        dialogStage.showAndWait()
    }

    /**
     * Загрузка данных из файла
     */
    fun loadPersonDataFromFile(file: File) {
        try {
            val context = JAXBContext
                    .newInstance(WorkersListWrapper::class.java)
            val um = context.createUnmarshaller()
            val wrapper = um.unmarshal(file) as WorkersListWrapper
            workersData.clear()
            workersData.addAll(wrapper.workers)
            setFilePath(file)
        } catch (e: Exception) {
            val dialog = Alert(Alert.AlertType.ERROR)
            dialog.title = "Ошибка"
            dialog.headerText = "Невозможно загрузить информацию из файла:\n${file.path}"
            dialog.contentText = e.message
            dialog.showAndWait()
        }
    }

    /**
     * Сохранение данных таблицы в файл
     */
    fun saveWorkersDataToFile(file: File) {
        try {
            val context = JAXBContext
                    .newInstance(WorkersListWrapper::class.java)
            val m = context.createMarshaller()
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            val wrapper = WorkersListWrapper()
            wrapper.workers = workersData
            m.marshal(wrapper, file)
            setFilePath(file)
        } catch (e: Exception) {
            val dialog = Alert(Alert.AlertType.ERROR)
            dialog.title = "Ошибка"
            dialog.headerText = "Невозможно сохранить информацию в файл:\n${file.path}"
            dialog.contentText = e.message
            dialog.showAndWait()
        }
    }

    /**
     * Установка пути к файлу
     */
    fun setFilePath(file: File?) {
        val prefs = Preferences.userNodeForPackage(MainApp::class.java)
        if (file != null) {
            prefs.put("filePath", file.path)
            primaryStage.title = "Список работников - " + file.name
        } else {
            prefs.remove("filePath")
            primaryStage.title = "Список работников"
        }
    }

    /**
     * Получение пути к файлу
     */
    fun getFilePath(): File? {
        val filePath = Preferences.userNodeForPackage(MainApp::class.java).get("filePath", null)
        if (filePath != null) {
            return File(filePath)
        } else {
            return null
        }
    }
}