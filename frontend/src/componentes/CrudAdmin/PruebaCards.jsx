import { useState } from "react";
import { CardComponent } from "./CardComponent";
import { TableCompras } from "./TableCompras";
import { TableDespachos } from "./TableDespachos";
import axios from "axios";
import Swal from "sweetalert2";

export const PruebaCards = () => {
  const [tablaCompras, setTablaCompras] = useState(false);
  const [tablaOrdenes, setTablaOrdenes] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    direccionCompra: "",
    valorCompra: "",
    fechaCompra: "",
    despachoGenerado: false,
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleCrearVenta = async (e) => {
    e.preventDefault();
    try {
      await axios.post("/api/ventas/api/v1/ventas", {
        ...form,
        valorCompra: parseInt(form.valorCompra),
        despachoGenerado: false,
      }, {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
      });
      Swal.fire({
        title: "Venta creada 💰!",
        text: "La orden de compra fue registrada exitosamente",
        icon: "success",
        confirmButtonText: "Aceptar",
      });
      setShowForm(false);
      setForm({ direccionCompra: "", valorCompra: "", fechaCompra: "", despachoGenerado: false });
      setTablaCompras(true);
      setTablaOrdenes(false);
    } catch (error) {
      console.error("Error al crear venta:", error);
      Swal.fire({
        title: "Error",
        text: "No se pudo crear la venta",
        icon: "error",
        confirmButtonText: "Aceptar",
      });
    }
  };

  return (
    <section>
      <div className="flex justify-center flex-wrap gap-4">
        <CardComponent
          title="Consultar Ordenes de compra 💰"
          description="Revisa las últimas oc realizadas para generar su despacho"
          buttonText="Consultar"
          onClick={() => {
            setTablaCompras(true);
            setTablaOrdenes(false);
            setShowForm(false);
          }}
        />
        <CardComponent
          title="Revisar Ordenes de despacho 🚚"
          description="Consulta los despachos realizados, modifica los registros de intentos o cierra la orden"
          buttonText="Consultar"
          onClick={() => {
            setTablaCompras(false);
            setTablaOrdenes(true);
            setShowForm(false);
          }}
        />
        <CardComponent
          title="Crear Orden de compra ➕"
          description="Registra una nueva orden de compra para generar su despacho"
          buttonText="Crear"
          onClick={() => {
            setShowForm(true);
            setTablaCompras(false);
            setTablaOrdenes(false);
          }}
        />
      </div>

      {/* Formulario para crear venta */}
      {showForm && (
        <div className="flex justify-center mt-8">
          <div className="bg-white border border-gray-200 rounded-lg shadow p-8 w-full max-w-md">
            <h2 className="text-2xl font-bold text-teal-600 text-center mb-6">
              Nueva Orden de Compra
            </h2>
            <form onSubmit={handleCrearVenta} className="flex flex-col gap-4">
              <div>
                <label className="block font-bold mb-1">Dirección de entrega</label>
                <input
                  type="text"
                  name="direccionCompra"
                  value={form.direccionCompra}
                  onChange={handleChange}
                  placeholder="Ej: Av. Providencia 1234, Santiago"
                  className="border border-gray-300 rounded-lg block w-full p-2"
                  required
                />
              </div>
              <div>
                <label className="block font-bold mb-1">Valor de compra</label>
                <input
                  type="number"
                  name="valorCompra"
                  value={form.valorCompra}
                  onChange={handleChange}
                  placeholder="Ej: 150000"
                  className="border border-gray-300 rounded-lg block w-full p-2"
                  required
                />
              </div>
              <div>
                <label className="block font-bold mb-1">Fecha de compra</label>
                <input
                  type="date"
                  name="fechaCompra"
                  value={form.fechaCompra}
                  onChange={handleChange}
                  className="border border-gray-300 rounded-lg block w-full p-2"
                  required
                />
              </div>
              <button
                type="submit"
                className="py-3 px-8 rounded-lg bg-teal-600 text-white font-bold hover:bg-teal-700 transition-all duration-300"
              >
                Registrar Orden
              </button>
              <button
                type="button"
                onClick={() => setShowForm(false)}
                className="py-3 px-8 rounded-lg bg-gray-200 text-gray-700 font-bold hover:bg-gray-300 transition-all duration-300"
              >
                Cancelar
              </button>
            </form>
          </div>
        </div>
      )}

      <section>
        {tablaCompras && <TableCompras />}
        {tablaOrdenes && <TableDespachos />}
      </section>
    </section>
  );
};