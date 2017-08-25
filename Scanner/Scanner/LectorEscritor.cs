using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Scanner
{
    public class LectorEscritor
    {
        //copiar codigo
        public void CargarArchivo(string nombreArchivo)
        {
            // Variables locales
            FileInfo informacionArchivo = null;
            StreamReader lectorArchivo = null;

            string linea = "";

            //Se quitan los espacios del nombre del archivo
            nombreArchivo = nombreArchivo.Trim();

            //Se valida que se ingrese un nombre de archivo
            if (nombreArchivo.CompareTo("") == 0)
            {
                throw new Exception("Se debe ingresar un nombre de archivo.");
            }

            //Se valida que el archivo exista
            informacionArchivo = new FileInfo(nombreArchivo);

            if (!informacionArchivo.Exists)
            {
                throw new Exception("Debe ingresar un nombre de archivo existente.");
            }

            //Se abre el archivo para lectura
            lectorArchivo = new StreamReader(nombreArchivo);

            //Se recorre el archivo guardando su contenido en memoria
            char[] vacios = { ' ', '\t' };
            while (linea != null)
            {
                // Se obtiene la siguiente linea
                linea = lectorArchivo.ReadLine();
                //Separamos cada palabra por caracteres vacíos (espacios, saltos de línea y tabulaciones)
                string[] palabras = linea.Split(vacios);
                foreach (string s in palabras)
                {
                    string pertenencia = s + " = " + Tomatoken(s);
                    Console.WriteLine(pertenencia);
                }
            }
        }
        List<int> charset = new List<int>();
        public void Llenarcharset()
        {
            charset.Add(32);
            charset.Add(33);
            charset.Add(34);
            charset.Add(35);
            charset.Add(36);
            charset.Add(37);
            charset.Add(38);
            charset.Add(39);
            charset.Add(40);
            charset.Add(41);
            charset.Add(42);
            charset.Add(43);
            charset.Add(44);
            charset.Add(45);
            charset.Add(46);
            charset.Add(47);
            charset.Add(48);
            charset.Add(49);
            charset.Add(50);
            charset.Add(51);
            charset.Add(52);
            charset.Add(53);
            charset.Add(54);
            charset.Add(55);
            charset.Add(56);
            charset.Add(57);
            charset.Add(58);
            charset.Add(59);
            charset.Add(60);
            charset.Add(61);
            charset.Add(62);
            charset.Add(63);
            charset.Add(64);
            charset.Add(65);
            charset.Add( 66);
            charset.Add(67);
            charset.Add(68);
            charset.Add(69);
            charset.Add(70);
            charset.Add(71);
            charset.Add(72);
            charset.Add(73);
            charset.Add(74);
            charset.Add(75);
            charset.Add(76);
            charset.Add(77);
            charset.Add(78);
            charset.Add(79);
            charset.Add(80);
            charset.Add(81);
            charset.Add(82);
            charset.Add(83);
            charset.Add(84);
            charset.Add(85);
            charset.Add(86);
            charset.Add(87);
            charset.Add(88);
            charset.Add(89);
            charset.Add(90);
            charset.Add( 91);
            charset.Add( 92);
            charset.Add(93);
            charset.Add(94);
            charset.Add(95);
            charset.Add(96);
            charset.Add(97);
            charset.Add(98);
            charset.Add(99);
            charset.Add(100);
            charset.Add(101);
            charset.Add(102);
            charset.Add(103);
            charset.Add(104);
            charset.Add(105);
            charset.Add( 106);
            charset.Add(107);
            charset.Add(108);
            charset.Add(109);
            charset.Add(110);
            charset.Add(111);
            charset.Add(112);
            charset.Add(113);
            charset.Add(114);
            charset.Add(115);
            charset.Add(116);
            charset.Add(117);
            charset.Add(118);
            charset.Add(119);
            charset.Add(120);
            charset.Add(121);
            charset.Add(122);
            charset.Add(123);
            charset.Add(124);
            charset.Add(125);
            charset.Add(126);
            charset.Add(127);
            charset.Add(128);
            charset.Add(129);
            charset.Add(130);
            charset.Add(131);
            charset.Add(132);
            charset.Add(133);
            charset.Add(134);
            charset.Add(135);
            charset.Add(136);
            charset.Add(137);
            charset.Add(138);
            charset.Add(139);
            charset.Add(140);
            charset.Add(141);
            charset.Add(142);
            charset.Add(143);
            charset.Add(144);
            charset.Add(145);
            charset.Add(146);
            charset.Add(147);
            charset.Add(148);
            charset.Add(149);
            charset.Add(150);
            charset.Add(151);
            charset.Add(152);
            charset.Add(153);
            charset.Add(154);
            charset.Add(155);
            charset.Add(156);
            charset.Add(157);
            charset.Add(158);
            charset.Add(159);
            charset.Add(160);
            charset.Add(161);
            charset.Add(162);
            charset.Add(163);
            charset.Add(164);
            charset.Add(165);
            charset.Add(166);
            charset.Add(167);
            charset.Add(168);
            charset.Add(169);
            charset.Add(170);
            charset.Add(171);
            charset.Add(172);
            charset.Add(173);
            charset.Add( 174);
            charset.Add( 175);
            charset.Add(176);
            charset.Add(177);
            charset.Add( 178);
            charset.Add(179);
            charset.Add(180);
            charset.Add(181);
            charset.Add(182);
            charset.Add(183);
            charset.Add(184);
            charset.Add(185);
            charset.Add(186);
            charset.Add(187);
            charset.Add(188);
            charset.Add(189);
            charset.Add(190);
            charset.Add(191);
            charset.Add(192);
            charset.Add(193);
            charset.Add(194);
            charset.Add(195);
            charset.Add(196);
            charset.Add(197);
            charset.Add(198);
            charset.Add(199);
            charset.Add(200);
            charset.Add(201);
            charset.Add(202);
            charset.Add(203);
            charset.Add( 204);
            charset.Add( 205);
            charset.Add(206);
            charset.Add( 207);
            charset.Add(208);
            charset.Add(209);
            charset.Add(210);
            charset.Add(211);
            charset.Add(212);
            charset.Add( 213);
            charset.Add(0, 214);
            charset.Add(0, 215);
            charset.Add(0, 216);
            charset.Add(0, 217);
            charset.Add(0, 218);
            charset.Add(0, 219);
            charset.Add(0, 220);
            charset.Add(0, 221);
            charset.Add(0, 222);
            charset.Add(0, 223);
            charset.Add(0, 224);
            charset.Add(0, 225);
            charset.Add(0, 226);
            charset.Add(0, 227);
            charset.Add(0, 228);
            charset.Add(0, 229);
            charset.Add(0, 230);
            charset.Add(0, 231);
            charset.Add(0, 232);
            charset.Add(0, 233);
            charset.Add(0, 234);
            charset.Add(0, 235);
            charset.Add(0, 236);
            charset.Add(0, 237);
            charset.Add(0, 238);
            charset.Add(0, 239);
            charset.Add(0, 240);
            charset.Add(0, 241);
            charset.Add(0, 242);
            charset.Add(0, 243);
            charset.Add(0, 244);
            charset.Add(0, 245);
            charset.Add(0, 246);
            charset.Add(0, 247);
            charset.Add(0, 248);
            charset.Add(0, 249);
            charset.Add(0, 250);
            charset.Add(0, 251);
            charset.Add(0, 252);
            charset.Add(0, 253);
            charset.Add(0, 254);
        }
        List<int> letra = new List<int>();
        public void Llenarletra()
        {
            letra.Add(97);
            letra.Add(98);
            letra.Add(99);
            letra.Add(100);
            letra.Add(101);
            letra.Add(102);
            letra.Add(103);
            letra.Add(104);
            letra.Add(105);
            letra.Add(106);
            letra.Add(107);
            letra.Add(108);
            letra.Add(109);
            letra.Add(110);
            letra.Add(111);
            letra.Add(112);
            letra.Add(113);
            letra.Add(114);
            letra.Add(115);
            letra.Add(116);
            letra.Add(117);
            letra.Add(118);
            letra.Add(119);
            letra.Add(120);
            letra.Add(121);
            letra.Add(122);
            letra.Add(97);
            letra.Add(98);
            letra.Add(99);
            letra.Add(100);
            letra.Add(101);
            letra.Add(102);
            letra.Add(103);
            letra.Add(104);
            letra.Add(105);
            letra.Add(106);
            letra.Add(107);
            letra.Add(108);
            letra.Add(109);
            letra.Add(110);
            letra.Add(111);
            letra.Add(112);
            letra.Add(113);
            letra.Add(114);
            letra.Add(115);
            letra.Add(116);
            letra.Add(117);
            letra.Add(118);
            letra.Add(119);
            letra.Add(120);
            letra.Add(121);
            letra.Add(122);
            letra.Add(95);
            letra.Add(95);
        }
        List<int> digito = new List<int>();
        public void Llenardigito()
        {
            digito.Add(48);
            digito.Add(49);
            digito.Add(50);
            digito.Add(51);
            digito.Add(52);
            digito.Add(53);
            digito.Add(54);
            digito.Add(55);
            digito.Add(56);
            digito.Add(57);
        }
        private int Tomatoken(string palabra)
        {
            int estado = 0;
            int contador = 0;
            int tacos = 0; //token
            bool aceptacion = false;
            bool reserved = false;
            string[] Reservadas = { "4='break'", "5='case'", "6='char'", "7='const'", "8='default'", "9='do'", "10='else'", "11='false'", "12='for'", "13='if'", "14='int'", "15='ref'", "16='return'", "17='switch'", "18='true'", "19='void'", "20='while'", "21='public'", "22='class'" };
            int[] numReservadas = new int[Reservadas.Length];
            //string salida = "";
            Llenarcharset();
            Llenarletra();
            Llenardigito();

            //Verificar si la palabara es reservada
            for (int i = 0; i < Reservadas.Length; i++)
            {
                Reservadas[i] = Reservadas[i].Replace('-', '"');
                string[] aux = Reservadas[i].Split('=');
                numReservadas[i] = Convert.ToInt32(aux[0]);
                if (aux.Length > 2)
                {
                    string pal = "";
                    for (int j = 1; j < aux.Length; j++)
                    {
                        pal = pal + aux[j] + "=";
                    }
                    //pal = pal.Substring(1, pal.Length - 2);
                    Reservadas[i] = pal;
                }
                else
                {
                    Reservadas[i] = aux[1].Substring(1, aux[1].Length - 2);
                }
            }
            for (int i = 0; i < Reservadas.Length; i++)
            {
                if (palabra == Reservadas[i])
                {
                    tacos = numReservadas[i];
                    aceptacion = true;
                    reserved = true;
                    break;
                }
            }
            if (!reserved)
            {
                palabra += "#";
               while (contador < palabra.Length)
                {
                    switch (estado)
                    {
                        case 0:
                            {
                                switch (palabra[contador])
                                {
                                    case '\'':
                                    {
                                            tacos = 2;
                                            estado = 1;
                                            break;
                                        }
                                    case '~':
                                        {
                                            tacos = 4;
                                            estado = 9;
                                            break;
                                        }
                                    case '/':
                                        {
                                            tacos = 23;
                                            estado = 5;
                                            break;
                                        }
                                    case '{':
                                        {
                                            tacos = 25;
                                            estado = 9;
                                            break;
                                        }
                                    case '}':
                                        {
                                            tacos = 26;
                                            estado = 9;
                                            break;
                                        }
                                    case ',':
                                        {
                                            tacos = 27;
                                            estado = 9;
                                            break;
                                        }
                                    case '(':
                                        {
                                            tacos = 28;
                                            estado = 9;
                                            break;
                                        }
                                    case ')':
                                        {
                                            tacos = 29;
                                            estado = 9;
                                            break;
                                        }
                                    case '-':
                                        {
                                            tacos = 31;
                                            estado = 6;
                                            break;
                                        }
                                    case '!':
                                        {
                                            tacos = 33;
                                            estado = 7;
                                            break;
                                        }
                                    case '<':
                                        {
                                            tacos = 37;
                                            estado = 8;
                                            break;
                                        }
                                    case '>':
                                        {
                                            tacos = 38;
                                            estado = 9;
                                            break;
                                        }
                                    case '=':
                                        {
                                            tacos = 41;
                                            estado = 10;
                                            break;
                                        }
                                    case '&':
                                        {
                                            tacos = 43;
                                            estado = 9;
                                            break;
                                        }
                                    case '|':
                                        {
                                            tacos = 44;
                                            estado = 9;
                                            break;
                                        }
                                    case ';':
                                        {
                                            tacos = 46;
                                            estado = 9;
                                            break;
                                        }
                                    case ':':
                                        {
                                            tacos = 47;
                                            estado = 9;
                                            break;
                                        }
                                    default:
                                        {
                                            if (digito.Contains((int)palabra[contador]))
                                            {
                                                tacos = 1;
                                                estado = 2;
                                            }
                                            else if (letra.Contains((int)palabra[contador]))
                                            {
                                                tacos = 3;
                                                estado = 3;
                                            }

                                            break;
                                        }

                                }
                                aceptacion = false;
                                break;
                            }
                        case 1:
                            {
                                switch (palabra[contador])
                                {
                                    default:
                                        {
                                            if (charset.Contains((int)palabra[contador]))
                                            {
                                                tacos = 2;
                                                estado = 11;
                                            }

                                            break;
                                        }

                                }
                                aceptacion = false;
                                break;
                            }
                        case 2:
                            {
                                switch (palabra[contador])
                                {
                                    default:
                                        {
                                            if (digito.Contains((int)palabra[contador]))
                                            {
                                                tacos = 1;
                                                estado = 2;
                                            }

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 3:
                            {
                                switch (palabra[contador])
                                {
                                    default:
                                        {
                                            if (letra.Contains((int)palabra[contador]))
                                            {
                                                tacos = 3;
                                                estado = 3;
                                            }
                                            else if (digito.Contains((int)palabra[contador]))
                                            {
                                                tacos = 3;
                                                estado = 3;
                                            }

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 4:
                            {
                                switch (palabra[contador])
                                {
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 5:
                            {
                                switch (palabra[contador])
                                {
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 6:
                            {
                                switch (palabra[contador])
                                {
                                    case '-':
                                        {
                                            tacos = 31;
                                            estado = 9;
                                            break;
                                        }
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 7:
                            {
                                switch (palabra[contador])
                                {
                                    case '=':
                                        {
                                            tacos = 42;
                                            estado = 9;
                                            break;
                                        }
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 8:
                            {
                                switch (palabra[contador])
                                {
                                    case '=':
                                        {
                                            tacos = 39;
                                            estado = 9;
                                            break;
                                        }
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 9:
                            {
                                switch (palabra[contador])
                                {
                                    case '=':
                                        {
                                            tacos = 40;
                                            estado = 9;
                                            break;
                                        }
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 10:
                            {
                                switch (palabra[contador])
                                {
                                    case '=':
                                        {
                                            tacos = 41;
                                            estado = 9;
                                            break;
                                        }
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = true;
                                break;
                            }
                        case 11:
                            {
                                switch (palabra[contador])
                                {
                                    case '\'':
                                    {
                                            tacos = 2;
                                            estado = 9;
                                            break;
                                        }
                                    default:
                                        {

                                            break;
                                        }

                                }
                                aceptacion = false;
                                break;
                            }


                        default:
                            {
                                break;
                            }
                    }
                    contador++;
                }
            }
            if (!aceptacion)
            {
                return 48;
            }
            return tacos;
        }
    }
}
