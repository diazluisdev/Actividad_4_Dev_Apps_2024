const express = require('express');
const router = express.Router();
const gastosController = require('../controllers/gastosController');

// Rutas para gestionar los gastos
router.get('/', gastosController.getAllGastos);
router.get('/buscar/:id', gastosController.getGastoById);
router.post('/registrar/', gastosController.createGasto);
router.put('/actualizar/:id', gastosController.updateGasto);
router.delete('/eliminar/:id', gastosController.deleteGasto);

module.exports = router;
