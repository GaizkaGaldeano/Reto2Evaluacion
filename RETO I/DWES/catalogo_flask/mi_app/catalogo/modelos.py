from mi_app import db
class Producto(db.Model):
    id =db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(255))
    precio = db.Column(db.Float)
    def __init__(self, nombre, precio):
        self.nombre = nombre
        self.precio = precio
    def __repr__(self):
        return f'<Producto {self.id}>'
    

    