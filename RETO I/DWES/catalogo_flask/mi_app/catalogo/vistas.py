from flask import request, jsonify, Blueprint
from mi_app import db
from mi_app.catalogo.modelos import Producto
catalog = Blueprint('catalog', __name__)
@catalog.route('/')
@catalog.route('/home')
def home():
    return "Catálogo de Productos"
@catalog.route('/producto/<id>')
def producto(id):
    producto = Producto.query.get_or_404(id)
    return f"Producto: {producto.nombre}, Precio: {producto.precio}"
@catalog.route('/productos')
def productos():
    productos = Producto.query.all()
    res = {}
    for producto in productos:
        res[producto.id] = {
            'nombre': producto.nombre,
            'precio': str(producto.precio)
        }
    return jsonify(res)
    
@catalog.route('/producto-crear', methods=['POST'])
def crear_producto():
    nombre = request.args.get('nombre')
    precio = request.args.get('precio')
    producto = Producto(nombre, precio)
    db.session.add(producto)
    db.session.commit()
    return f"Producto {producto.nombre} creado con ID {producto.id}"