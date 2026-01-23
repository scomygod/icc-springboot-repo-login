#!/usr/bin/env python3
"""
Script para cargar datos de prueba en la base de datos
Crea 1000+ productos con usuarios y categorías variadas

Uso:
    python3 load_data.py

Requisitos:
    pip install requests
"""

import requests
import random
import json
from typing import List, Dict

# ============== CONFIGURACIÓN ==============

BASE_URL = "http://localhost:8080/api"
TOTAL_PRODUCTS = 1200  # Total de productos a crear

# ============== DATOS INICIALES ==============

USERS_DATA = [
    {"name": "Juan Pérez", "email": "juan.perez@email.com", "password": "password123"},
    {"name": "María García", "email": "maria.garcia@email.com", "password": "password123"},
    {"name": "Carlos López", "email": "carlos.lopez@email.com", "password": "password123"},
    {"name": "Ana Martínez", "email": "ana.martinez@email.com", "password": "password123"},
    {"name": "Pedro Rodríguez", "email": "pedro.rodriguez@email.com", "password": "password123"},
    {"name": "Laura Fernández", "email": "laura.fernandez@email.com", "password": "password123"},
    {"name": "Diego Sánchez", "email": "diego.sanchez@email.com", "password": "password123"},
]

CATEGORIES_DATA = [
    {"name": "Electrónicos", "description": "Dispositivos y aparatos electrónicos"},
    {"name": "Gaming", "description": "Productos para videojuegos y entretenimiento"},
    {"name": "Computación", "description": "Equipos de cómputo y accesorios"},
    {"name": "Hogar", "description": "Artículos para el hogar y decoración"},
    {"name": "Deportes", "description": "Equipamiento deportivo y fitness"},
    {"name": "Moda", "description": "Ropa, calzado y accesorios"},
    {"name": "Libros", "description": "Libros físicos y digitales"},
    {"name": "Música", "description": "Instrumentos musicales y audio"},
    {"name": "Smartphones", "description": "Teléfonos móviles y accesorios"},
    {"name": "Audio", "description": "Audífonos, bocinas y equipos de sonido"},
]

PRODUCT_PREFIXES = [
    "Laptop", "Mouse", "Teclado", "Monitor", "Auriculares",
    "Smartphone", "Tablet", "Cámara", "Impresora", "Disco Duro",
    "Memoria RAM", "Procesador", "Tarjeta Gráfica", "Consola",
    "Control", "Micrófono", "Webcam", "Router", "Switch", "Cable",
    "Escritorio", "Silla", "Lámpara", "Ventilador", "Altavoz"
]

ADJECTIVES = [
    "Gaming", "Pro", "Ultra", "Max", "Plus", "Premium",
    "Inalámbrico", "RGB", "Mecánico", "Portátil", "Compacto",
    "Profesional", "Avanzado", "Básico", "Económico", "Profesional",
    "Deportivo", "Elegante", "Resistente", "Ligerísimo", "Potente"
]

# ============== COLORES PARA LA CONSOLA ==============

class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

# ============== FUNCIONES HELPER ==============

def print_header(text: str):
    """Imprime un encabezado con colores"""
    print(f"\n{Colors.BOLD}{Colors.HEADER}{'='*60}")
    print(f"{text}")
    print(f"{'='*60}{Colors.ENDC}\n")

def print_success(text: str, count: int = None):
    """Imprime un mensaje de éxito"""
    if count:
        print(f"{Colors.OKGREEN}✅ {text}: {count}{Colors.ENDC}")
    else:
        print(f"{Colors.OKGREEN}✅ {text}{Colors.ENDC}")

def print_info(text: str):
    """Imprime un mensaje informativo"""
    print(f"{Colors.OKCYAN}ℹ️  {text}{Colors.ENDC}")

def print_warning(text: str):
    """Imprime un mensaje de advertencia"""
    print(f"{Colors.WARNING}⚠️  {text}{Colors.ENDC}")

def print_error(text: str):
    """Imprime un mensaje de error"""
    print(f"{Colors.FAIL}❌ {text}{Colors.ENDC}")

# ============== FUNCIONES PARA CREAR DATOS ==============

def create_users() -> List[Dict]:
    """Crea usuarios en la base de datos"""
    print_info("Creando usuarios...")
    created_users = []
    
    for user_data in USERS_DATA:
        try:
            response = requests.post(
                f"{BASE_URL}/users",
                json=user_data,
                timeout=5
            )
            
            if response.status_code in [200, 201]:
                user = response.json()
                created_users.append(user)
                print_info(f"Usuario creado: {user_data['name']} (ID: {user['id']})")
            else:
                print_warning(f"No se pudo crear usuario: {user_data['name']} - Status: {response.status_code}")
        except Exception as e:
            print_error(f"Error creando usuario {user_data['name']}: {str(e)}")
    
    print_success("Usuarios creados", len(created_users))
    return created_users

def create_categories() -> List[Dict]:
    """Crea categorías en la base de datos"""
    print_info("Creando categorías...")
    created_categories = []
    
    for category_data in CATEGORIES_DATA:
        try:
            response = requests.post(
                f"{BASE_URL}/categories",
                json=category_data,
                timeout=5
            )
            
            if response.status_code in [200, 201]:
                # Intentar parsear JSON, si falla asumir éxito y consultar después
                try:
                    category = response.json()
                    created_categories.append(category)
                    print_info(f"Categoría creada: {category_data['name']} (ID: {category['id']})")
                except:
                    # Si no retorna JSON, buscar la categoría creada por nombre
                    print_warning(f"Categoría creada pero sin respuesta JSON: {category_data['name']}")
            else:
                print_warning(f"No se pudo crear categoría: {category_data['name']} - Status: {response.status_code}")
        except Exception as e:
            print_error(f"Error creando categoría {category_data['name']}: {str(e)}")
    
    # Si no se pudieron obtener IDs, consultar todas las categorías
    if not created_categories:
        print_info("Consultando categorías existentes...")
        try:
            response = requests.get(f"{BASE_URL}/categories", timeout=5)
            if response.status_code == 200:
                created_categories = response.json()
                print_success(f"Categorías obtenidas de BD", len(created_categories))
        except Exception as e:
            print_error(f"Error consultando categorías: {str(e)}")
    
    print_success("Categorías disponibles", len(created_categories))
    return created_categories

def create_products(users: List[Dict], categories: List[Dict], total: int):
    """Crea productos en la base de datos"""
    print_info(f"Creando {total} productos...")
    
    created_count = 0
    failed_count = 0
    
    for i in range(total):
        try:
            # Datos aleatorios del producto
            prefix = random.choice(PRODUCT_PREFIXES)
            adjective = random.choice(ADJECTIVES)
            name = f"{prefix} {adjective} {i+1}"
            
            # Precio aleatorio entre $10 y $5000
            price = round(10 + (random.random() * 4990), 2)
            
            # Descripción
            description = f"Descripción detallada del {name}. Producto de alta calidad con excelentes características."
            
            # Usuario aleatorio
            user_id = random.choice(users)['id']
            
            # Categorías aleatorias (entre 2 y 3 para cumplir requisito)
            num_categories = random.randint(2, 3)
            category_ids = set()
            while len(category_ids) < num_categories:
                category_ids.add(random.choice(categories)['id'])
            
            # Crear producto
            product_data = {
                "name": name,
                "price": price,
                "description": description,
                "userId": user_id,
                "categoryIds": list(category_ids)
            }
            
            response = requests.post(
                f"{BASE_URL}/products",
                json=product_data,
                timeout=5
            )
            
            if response.status_code in [200, 201]:
                created_count += 1
                # Log cada 100 productos
                if (created_count) % 100 == 0:
                    print_info(f"Productos creados: {created_count}/{total}")
            else:
                failed_count += 1
                if failed_count <= 5:  # Solo mostrar primeros 5 errores
                    print_warning(f"Error creando producto {name}: {response.status_code}")
        
        except Exception as e:
            failed_count += 1
            if failed_count <= 5:  # Solo mostrar primeros 5 errores
                print_warning(f"Excepción al crear producto: {str(e)}")
    
    print_success("Productos creados", created_count)
    if failed_count > 0:
        print_warning(f"Productos con error: {failed_count}")
    
    return created_count

def verify_connection():
    """Verifica que el servidor esté disponible"""
    try:
        response = requests.get(f"{BASE_URL}/products/all", timeout=5)
        if response.status_code == 200:
            print_success("Conexión con servidor establecida")
            return True
    except Exception as e:
        print_error(f"No se puede conectar con el servidor: {str(e)}")
        return False

def get_database_status() -> Dict:
    """Obtiene el estado actual de la base de datos"""
    try:
        # Obtener usuarios
        users_response = requests.get(f"{BASE_URL}/users", timeout=5)
        users_count = len(users_response.json()) if users_response.status_code == 200 else 0
        
        # Obtener categorías
        categories_response = requests.get(f"{BASE_URL}/categories", timeout=5)
        categories_count = len(categories_response.json()) if categories_response.status_code == 200 else 0
        
        # Obtener productos
        products_response = requests.get(f"{BASE_URL}/products/all", timeout=5)
        products_count = len(products_response.json()) if products_response.status_code == 200 else 0
        
        return {
            "users": users_count,
            "categories": categories_count,
            "products": products_count
        }
    except Exception as e:
        print_error(f"Error obteniendo estado de BD: {str(e)}")
        return {"users": 0, "categories": 0, "products": 0}

# ============== FUNCIÓN PRINCIPAL ==============

def main():
    """Función principal"""
    print_header("🚀 CARGADOR DE DATOS DE PRUEBA")
    
    # 1. Verificar conexión
    print_info("Verificando conexión con servidor...")
    if not verify_connection():
        print_error("No se puede conectar con el servidor en localhost:8080")
        print_info("Asegúrate de que Spring Boot está ejecutándose")
        return
    
    # 2. Obtener estado de BD
    print_info("Obteniendo estado de la base de datos...")
    status = get_database_status()
    print(f"\nEstado actual:")
    print(f"  - Usuarios: {status['users']}")
    print(f"  - Categorías: {status['categories']}")
    print(f"  - Productos: {status['products']}\n")
    
    if status['products'] > 0:
        print_warning("¡La base de datos ya contiene datos!")
        response = input("¿Deseas continuar y agregar más datos? (s/n): ")
        if response.lower() != 's':
            print_info("Operación cancelada")
            return
    
    # 3. Crear datos
    print_header("📦 CREAR DATOS")
    
    users = create_users()
    if not users:
        print_error("No se crearon usuarios. Abortando...")
        return
    
    categories = create_categories()
    if not categories:
        print_error("No se crearon categorías. Abortando...")
        return
    
    products_created = create_products(users, categories, TOTAL_PRODUCTS)
    
    # 4. Resumen final
    print_header("📊 RESUMEN FINAL")
    
    final_status = get_database_status()
    
    print(f"\n{Colors.BOLD}Base de datos final:{Colors.ENDC}")
    print(f"  - Usuarios: {Colors.OKGREEN}{final_status['users']}{Colors.ENDC}")
    print(f"  - Categorías: {Colors.OKGREEN}{final_status['categories']}{Colors.ENDC}")
    print(f"  - Productos: {Colors.OKGREEN}{final_status['products']}{Colors.ENDC}")
    
    print(f"\n{Colors.BOLD}{Colors.OKGREEN}✅ ¡Carga de datos completada exitosamente!{Colors.ENDC}\n")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print_warning("\nOperación cancelada por el usuario")
    except Exception as e:
        print_error(f"Error inesperado: {str(e)}")