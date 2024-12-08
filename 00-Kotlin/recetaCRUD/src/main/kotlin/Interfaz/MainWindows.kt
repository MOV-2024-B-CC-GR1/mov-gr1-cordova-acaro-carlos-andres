package Interfaz

import filemanager.FileManager
import models.Ingrediente
import models.Receta
import javax.swing.*
import java.awt.*
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel
import javax.swing.ListSelectionModel
import javax.swing.JTextField
import javax.swing.JCheckBox
import javax.swing.JComponent

class MainWindow : JFrame() {
    companion object {
        private const val WINDOW_WIDTH = 1000
        private const val WINDOW_HEIGHT = 600
        private const val SPLIT_PANE_DIVIDER_LOCATION = 300
        private const val PADDING = 10
        private const val FIELD_SIZE = 20
    }

    // UI Components
    private val recetasList = JList<String>()
    private val recetasModel = DefaultListModel<String>()
    private val ingredientesTable = JTable()
    private val ingredientesModel = createIngredientesTableModel()
    private var recetaActual: Receta? = null

    init {
        setupWindow()
        createMainLayout()
        updateLists()
    }

    private fun createIngredientesTableModel() = object : DefaultTableModel() {
        override fun isCellEditable(row: Int, column: Int) = false
    }

    private fun setupWindow() {
        title = "Gestor de Recetas"
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(WINDOW_WIDTH, WINDOW_HEIGHT)
        setLocationRelativeTo(null)
    }

    private fun createMainLayout() {
        val mainPanel = JSplitPane(JSplitPane.HORIZONTAL_SPLIT).apply {
            dividerLocation = SPLIT_PANE_DIVIDER_LOCATION
            leftComponent = createRecipesPanel()
            rightComponent = createDetailsPanel()
        }
        add(mainPanel)
    }

    private fun createRecipesPanel(): JPanel {
        return JPanel(BorderLayout(PADDING, PADDING)).apply {
            border = EmptyBorder(PADDING, PADDING, PADDING, PADDING)
            add(createRecipesTitlePanel(), BorderLayout.NORTH)
            add(createRecipesListPanel(), BorderLayout.CENTER)
            add(createNewRecipeButton(), BorderLayout.SOUTH)
        }
    }

    private fun createRecipesTitlePanel(): JPanel {
        return JPanel().apply {
            add(JLabel("Lista de Recetas", SwingConstants.CENTER).apply {
                font = Font("Arial", Font.BOLD, 16)
            })
        }
    }

    private fun createRecipesListPanel(): JScrollPane {
        recetasList.apply {
            model = recetasModel
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            addListSelectionListener {
                if (!it.valueIsAdjusting) {
                    showRecipeDetails()
                }
            }
        }
        return JScrollPane(recetasList)
    }

    private fun createNewRecipeButton(): JButton {
        return JButton("Nueva Receta").apply {
            addActionListener { showRecipeDialog() }
        }
    }

    private fun createDetailsPanel(): JPanel {
        return JPanel(BorderLayout(PADDING, PADDING)).apply {
            border = EmptyBorder(PADDING, PADDING, PADDING, PADDING)
            add(createRecipeDetailsPanel(), BorderLayout.NORTH)
            add(createIngredientsPanel(), BorderLayout.CENTER)
        }
    }

    private fun createRecipeDetailsPanel(): JPanel {
        return JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder("Detalles de la Receta")
            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(JLabel("Seleccione una receta para ver sus detalles"))
            })
        }
    }

    private fun createIngredientsPanel(): JPanel {
        setupIngredientsTable()

        return JPanel(BorderLayout()).apply {
            border = BorderFactory.createTitledBorder("Ingredientes")
            add(JScrollPane(ingredientesTable), BorderLayout.CENTER)
            add(createIngredientsButtonPanel(), BorderLayout.SOUTH)
        }
    }

    private fun setupIngredientsTable() {
        ingredientesModel.apply {
            setColumnIdentifiers(arrayOf("ID", "Nombre", "Cantidad", "Unidad", "Precio"))
        }
        ingredientesTable.apply {
            model = ingredientesModel
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        }
    }
    private fun createIngredientsButtonPanel(): JPanel {
        return JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(createAddIngredientButton())
            add(createEditIngredientButton())
            add(createDeleteIngredientButton())
        }
    }

    private fun createAddIngredientButton() = JButton("Agregar Ingrediente").apply {
        addActionListener {
            if (recetaActual != null) showIngredientDialog()
            else JOptionPane.showMessageDialog(this@MainWindow, "Seleccione una receta primero")
        }
    }

    private fun createEditIngredientButton() = JButton("Editar Ingrediente").apply {
        addActionListener { editSelectedIngredient() }
    }

    private fun createDeleteIngredientButton() = JButton("Eliminar Ingrediente").apply {
        addActionListener { deleteSelectedIngredient() }
    }

    private fun showRecipeDetails() {
        val selectedIndex = recetasList.selectedIndex
        if (selectedIndex >= 0) {
            val recetaId = recetasList.selectedValue.split(":")[0].toInt()
            recetaActual = FileManager.obtenerReceta(recetaId)
            recetaActual?.let { receta ->
                // Asegurarse de que la receta tenga sus ingredientes actualizados
                receta.ingredientes = FileManager.obtenerIngredientesDeReceta(receta.id).toMutableList()
                // Actualizar los cálculos
                receta.actualizarCalculos()

                val detallesPanel = (contentPane.getComponent(0) as JSplitPane)
                    .rightComponent as JPanel
                val panelSuperior = detallesPanel.getComponent(0) as JPanel

                panelSuperior.removeAll()
                panelSuperior.add(createRecipeInfoPanel(receta))
                panelSuperior.revalidate()
                panelSuperior.repaint()

                updateIngredientsTable(receta.id)
            }
        }
    }

    private fun createRecipeInfoPanel(receta: Receta): JPanel {
        return JPanel(GridBagLayout()).apply {
            border = BorderFactory.createTitledBorder("Detalles de la Receta")
            val gbc = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(5, 5, 5, 5)
            }

            addRecipeInfoRow(this, gbc, "Nombre:", receta.nombre, 0)
            addRecipeInfoRow(this, gbc, "Fecha de Creación:", receta.fechaCreacion.toString(), 1)
            addRecipeInfoRow(this, gbc, "Número de Ingredientes:", receta.numeroTotalIngredientes.toString(), 2)
            addRecipeInfoRow(this, gbc, "Es Vegana:", if (receta.esVegana) "Sí" else "No", 3)
            addRecipeInfoRow(this, gbc, "Costo Total:", "$${String.format("%.2f", receta.costoEstimado)} (calculado)", 4)

            add(createRecipeButtonPanel(), gbc.apply {
                gridy = 5
                gridwidth = 2
            })
        }
    }

    private fun addRecipeInfoRow(panel: JPanel, gbc: GridBagConstraints, label: String, value: String, row: Int) {
        gbc.apply {
            gridx = 0
            gridy = row
            gridwidth = 1
        }
        panel.add(JLabel(label), gbc)
        panel.add(JLabel(value), gbc.apply { gridx = 1 })
    }

    private fun createRecipeButtonPanel(): JPanel {
        return JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(JButton("Editar Receta").apply {
                addActionListener { editSelectedRecipe() }
            })
            add(JButton("Eliminar Receta").apply {
                addActionListener { deleteSelectedRecipe() }
            })
        }
    }

    private fun showRecipeDialog(receta: Receta = Receta()) {
        val dialog = createDialog(if(receta.id == 0) "Nueva Receta" else "Editar Receta")
        val nombreField = JTextField(receta.nombre, FIELD_SIZE)
        val fechaField = JTextField(receta.fechaCreacion.toString(), FIELD_SIZE)
        val veganaCheck = JCheckBox("", receta.esVegana)
        // Crear un label para el costo en lugar de un campo de texto
        val costoLabel = JLabel("$${String.format("%.2f", receta.costoEstimado)} (calculado de ingredientes)")

        dialog.apply {
            addDialogRow(this, "Nombre:", nombreField, 0)
            addDialogRow(this, "Fecha (YYYY-MM-DD):", fechaField, 1)
            addDialogRow(this, "Es Vegana:", veganaCheck, 2)
            addDialogRow(this, "Número de Ingredientes:", JLabel(receta.numeroTotalIngredientes.toString()), 3)
            addDialogRow(this, "Costo Total:", costoLabel, 4)

            // Agregar una nota explicativa
            val notaLabel = JLabel("<html><i>El costo se calcula automáticamente basado en los ingredientes</i></html>")
            addDialogRow(this, "", notaLabel, 5)

            add(createRecipeDialogButtonPanel(dialog, receta, nombreField, fechaField, veganaCheck),
                GridBagConstraints().apply {
                    gridx = 0
                    gridy = 6
                    gridwidth = 2
                })

            pack()
            setLocationRelativeTo(this@MainWindow)
            isVisible = true
        }
    }

    private fun createDialog(title: String): JDialog {
        return JDialog(this, title, true).apply {
            layout = GridBagLayout()
        }
    }

    private fun addDialogRow(dialog: JDialog, label: String, component: JComponent, row: Int) {
        val constraints = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = Insets(5, 5, 5, 5)
            gridx = 0
            gridy = row
        }
        dialog.add(JLabel(label), constraints)
        dialog.add(component, constraints.apply { gridx = 1 })
    }

    private fun createRecipeDialogButtonPanel(
        dialog: JDialog,
        receta: Receta,
        nombreField: JTextField,
        fechaField: JTextField,
        veganaCheck: JCheckBox
    ): JPanel {
        return JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(createSaveRecipeButton(dialog, receta, nombreField, fechaField, veganaCheck))
            add(createCancelButton(dialog))
        }
    }

    private fun createSaveRecipeButton(
        dialog: JDialog,
        receta: Receta,
        nombreField: JTextField,
        fechaField: JTextField,
        veganaCheck: JCheckBox
    ): JButton {
        return JButton("Guardar").apply {
            addActionListener {
                saveRecipe(dialog, receta, nombreField, fechaField, veganaCheck)
            }
        }
    }

    private fun saveRecipe(
        dialog: JDialog,
        receta: Receta,
        nombreField: JTextField,
        fechaField: JTextField,
        veganaCheck: JCheckBox
    ) {
        try {
            receta.apply {
                nombre = nombreField.text
                fechaCreacion = LocalDate.parse(fechaField.text)
                esVegana = veganaCheck.isSelected
                // El costoEstimado se actualiza automáticamente en actualizarCalculos()
                actualizarCalculos()
            }

            FileManager.guardarReceta(receta)
            updateLists()
            dialog.dispose()
        } catch (e: DateTimeParseException) {
            JOptionPane.showMessageDialog(dialog, "Formato de fecha inválido. Use YYYY-MM-DD")
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(dialog, "Error al guardar la receta: ${e.message}")
        }
    }

    private fun createCancelButton(dialog: JDialog) = JButton("Cancelar").apply {
        addActionListener { dialog.dispose() }
    }

    private fun showIngredientDialog(ingrediente: Ingrediente = Ingrediente()) {
        if (recetaActual == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una receta primero")
            return
        }

        val dialog = createDialog(if(ingrediente.id == 0) "Nuevo Ingrediente" else "Editar Ingrediente")
        val fields = createIngredientFields(ingrediente)

        dialog.apply {
            addIngredientDialogRows(this, fields)
            add(createIngredientButtonPanel(dialog, ingrediente, fields),
                GridBagConstraints().apply {
                    gridx = 0
                    gridy = 5
                    gridwidth = 2
                })
            pack()
            setLocationRelativeTo(this@MainWindow)
            isVisible = true
        }
    }

    private fun createIngredientFields(ingrediente: Ingrediente): Map<String, JComponent> {
        return mapOf(
            "nombre" to JTextField(ingrediente.nombre, FIELD_SIZE),
            "cantidad" to JTextField(ingrediente.cantidad.toString(), FIELD_SIZE),
            "unidad" to JTextField(ingrediente.unidadMedida, FIELD_SIZE),
            "principal" to JCheckBox("", ingrediente.esPrincipal),
            "costo" to JTextField(ingrediente.costoUnitario.toString(), FIELD_SIZE)
        )
    }

    private fun addIngredientDialogRows(dialog: JDialog, fields: Map<String, JComponent>) {
        addDialogRow(dialog, "Nombre:", fields["nombre"]!!, 0)
        addDialogRow(dialog, "Cantidad:", fields["cantidad"]!!, 1)
        addDialogRow(dialog, "Unidad de Medida:", fields["unidad"]!!, 2)
        addDialogRow(dialog, "Es Principal:", fields["principal"]!!, 3)
        addDialogRow(dialog, "Costo Unitario:", fields["costo"]!!, 4)
    }

    private fun createIngredientButtonPanel(
        dialog: JDialog,
        ingrediente: Ingrediente,
        fields: Map<String, JComponent>
    ): JPanel {
        return JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(createSaveIngredientButton(dialog, ingrediente, fields))
            add(createCancelButton(dialog))
        }
    }

    private fun createSaveIngredientButton(
        dialog: JDialog,
        ingrediente: Ingrediente,
        fields: Map<String, JComponent>
    ): JButton {
        return JButton("Guardar").apply {
            addActionListener {
                saveIngredient(dialog, ingrediente, fields)
            }
        }
    }

    private fun saveIngredient(
        dialog: JDialog,
        ingrediente: Ingrediente,
        fields: Map<String, JComponent>
    ) {
        try {
            ingrediente.apply {
                nombre = (fields["nombre"] as JTextField).text
                cantidad = (fields["cantidad"] as JTextField).text.toDoubleOrNull() ?: 0.0
                unidadMedida = (fields["unidad"] as JTextField).text
                esPrincipal = (fields["principal"] as JCheckBox).isSelected
                costoUnitario = (fields["costo"] as JTextField).text.toDoubleOrNull() ?: 0.0
                recetaId = recetaActual!!.id
            }

            // Guardar el ingrediente
            FileManager.guardarIngrediente(ingrediente)

            // Actualizar la receta actual con los ingredientes actualizados
            recetaActual?.let { receta ->
                // Obtener la lista actualizada de ingredientes
                receta.ingredientes = FileManager.obtenerIngredientesDeReceta(receta.id).toMutableList()
                // Actualizar los cálculos con la lista actualizada
                receta.actualizarCalculos()
                // Guardar la receta actualizada
                FileManager.guardarReceta(receta)
            }

            // Actualizar la interfaz
            updateIngredientsTable(recetaActual!!.id)
            showRecipeDetails() // Esto actualizará la visualización de los detalles
            dialog.dispose()
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(dialog, "Error al guardar el ingrediente: ${e.message}")
        }
    }

    private fun editSelectedRecipe() {
        recetaActual?.let { receta ->
            showRecipeDialog(receta)
        } ?: JOptionPane.showMessageDialog(this, "Por favor, seleccione una receta para editar")
    }

    private fun editSelectedIngredient() {
        val selectedRow = ingredientesTable.selectedRow
        if (selectedRow >= 0) {
            val ingredienteId = ingredientesTable.getValueAt(selectedRow, 0) as Int
            recetaActual?.let { receta ->
                FileManager.cargarIngredientes()
                    .find { it.id == ingredienteId && it.recetaId == receta.id }
                    ?.let { showIngredientDialog(it) }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un ingrediente para editar")
        }
    }

    private fun deleteSelectedRecipe() {
        recetaActual?.let { receta ->
            if (confirmDeletion("¿Está seguro de eliminar esta receta y todos sus ingredientes?")) {
                FileManager.eliminarReceta(receta.id)
                updateLists()
            }
        } ?: JOptionPane.showMessageDialog(this, "Por favor, seleccione una receta para eliminar")
    }

    private fun deleteSelectedIngredient() {
        val selectedRow = ingredientesTable.selectedRow
        if (selectedRow >= 0) {
            val ingredienteId = ingredientesTable.getValueAt(selectedRow, 0) as Int
            recetaActual?.let { receta ->
                if (confirmDeletion("¿Está seguro de eliminar este ingrediente?")) {
                    // Pasar tanto el ID del ingrediente como el ID de la receta
                    FileManager.eliminarIngrediente(ingredienteId, receta.id)

                    // Actualizar la lista de ingredientes después de eliminar
                    receta.ingredientes = FileManager.obtenerIngredientesDeReceta(receta.id).toMutableList()
                    // Recalcular totales
                    receta.actualizarCalculos()
                    // Guardar la receta actualizada
                    FileManager.guardarReceta(receta)
                    // Actualizar la interfaz
                    updateIngredientsTable(receta.id)
                    showRecipeDetails()
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un ingrediente para eliminar")
        }
    }


    private fun confirmDeletion(message: String): Boolean {
        return JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION
    }

    private fun updateIngredientsTable(recetaId: Int) {
        ingredientesModel.setRowCount(0)
        FileManager.obtenerIngredientesDeReceta(recetaId).forEach { ingrediente ->
            ingredientesModel.addRow(arrayOf(
                ingrediente.id,
                ingrediente.nombre,
                ingrediente.cantidad,
                ingrediente.unidadMedida,
                "%.2f".format(ingrediente.costoUnitario)
            ))
        }
    }

    private fun updateLists() {
        recetasModel.clear()
        FileManager.cargarRecetas().forEach { receta ->
            recetasModel.addElement("${receta.id}: ${receta.nombre}")
        }

        if (recetasList.selectedIndex < 0) {
            clearDetailsPanel()
        }
    }

    private fun clearDetailsPanel() {
        ingredientesModel.setRowCount(0)
        recetaActual = null

        val detallesPanel = (contentPane.getComponent(0) as JSplitPane)
            .rightComponent as JPanel
        val panelSuperior = detallesPanel.getComponent(0) as JPanel

        panelSuperior.removeAll()
        panelSuperior.add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Seleccione una receta para ver sus detalles"))
        })
        panelSuperior.revalidate()
        panelSuperior.repaint()
    }
}