using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
namespace Scanner
{
    class Program
    {
        [STAThread]
        static void Main(string[] args)
        {
            try
            {
                //OpenFileDialog ofd = new OpenFileDialog();
                Console.WriteLine("Ingrese la ruta del archivo de entrada");
                string nombre = Console.ReadLine();
                LectorEscritor macho = new LectorEscritor();
                macho.CargarArchivo(nombre);
            }
            catch(Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
            Console.ReadLine();
        }
    }
}
