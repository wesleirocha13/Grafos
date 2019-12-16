
package trabalhografos;

import br.com.davesmartins.grafos.grafos.lib.base.grafo.Aresta;
import br.com.davesmartins.grafos.grafos.lib.base.grafo.ETipoGrafo;
import br.com.davesmartins.grafos.grafos.lib.base.grafo.Vertice;
import br.com.davesmartins.grafos.grafos.lib.base.impl.GraphBaseMatrizIncidencia;
import br.com.davesmartins.grafos.grafos.lib.base.impl.IGraph2Semana;
import br.com.davesmartins.grafos.grafos.lib.base.impl.IGraph3Semana;
import br.com.davesmartins.grafos.grafos.lib.base.impl.IGraph4Semana;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class matrizIncidencia extends GraphBaseMatrizIncidencia implements IGraph2Semana, IGraph3Semana, IGraph4Semana {

	List<Vertice> vertices = new ArrayList<Vertice>();
	List<Aresta> arestas = new ArrayList<Aresta>();
	List<Aresta> arestasSimples = new ArrayList<Aresta>();
	HashMap<Aresta, Integer> valorArestas = new HashMap<Aresta, Integer>();
	ETipoGrafo eTipoGrafo = ETipoGrafo.NaoOrientado;
	Map<Vertice, Boolean> Visitado = new HashMap<Vertice, Boolean>();
	ArrayList<Vertice> menorCaminho = new ArrayList<Vertice>();
	Map<Vertice, Vertice> Pai = new HashMap<Vertice, Vertice>();

	@Override
	public String[][] getMatrizIncidencia() {
		String[][] matriz = new String[vertices.size()][arestas.size()];

		if (eTipoGrafo == ETipoGrafo.NaoOrientado) {
			for (Vertice v : vertices) {
				for (Aresta a : arestas) {
					if (v == a.getOrigem() && v == a.getDestino())
						matriz[vertices.indexOf(v)][arestas.indexOf(a)] = "2";
					if (v == a.getOrigem() || v == a.getDestino())
						matriz[vertices.indexOf(v)][arestas.indexOf(a)] = "1";
					else
						matriz[vertices.indexOf(v)][arestas.indexOf(a)] = "0";
				}
			}
		}

		else {
			for (Vertice v : vertices) {
				for (Aresta a : arestas) {
					if (v == a.getOrigem()) {
						matriz[vertices.indexOf(v)][arestas.indexOf(a)] = "1";
					} else {
						if (v == a.getDestino()) {
							matriz[vertices.indexOf(v)][arestas.indexOf(a)] = "-1";
						} else {
							matriz[vertices.indexOf(v)][arestas.indexOf(a)] = "0";
						}
					}
				}
			}
		}

		return matriz;

	}

	@Override
	public String getGrupo() {
		return "Grupo E";
	}

	@Override
	public String[] getMembros() {
		String[] membros = { "Caio Marques", "Carlos Eduardo", "Weslei Rocha" };
		return membros;
	}

	@Override
	public int getOrdem() {
		return vertices.size();
	}

	@Override
	public int getNumeroArestas() {
		return arestas.size();
	}

	@Override
	public int getGrau(Vertice vrtc) {
		int grau = 0;

		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem() && vrtc == a.getDestino()) {
				grau = grau + 2;
			} else {
				if (vrtc == a.getOrigem() || vrtc == a.getDestino()) {
					grau = grau + 1;
				}
			}
		}

		return grau;
	}

	@Override
	public int getGrauEmissao(Vertice vrtc) {
		int grau = 0;

		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem()) {
				grau = grau + 1;
			}
		}

		return grau;
	}

	@Override
	public int getGrauRecpcao(Vertice vrtc) {
		int grau = 0;

		for (Aresta a : arestas) {
			if (vrtc == a.getDestino()) {
				grau = grau + 1;
			}
		}

		return grau;
	}

	@Override
	public List<Vertice> getVertices() {
		List<Vertice> v = new ArrayList<Vertice>(vertices);
		return v;
	}

	@Override
	public void clickAddVertice(String string) {
		string = string.toUpperCase();

		boolean existe = false;
		for (Vertice v : vertices) {
			if (v.getNome().contains(string)) {
				existe = true;
			}
		}
		if (!existe) {
			Vertice v = new Vertice(string);
			vertices.add(v);
		}
	}

	@Override
	public void clickAddAresta(Vertice vrtc, Vertice vrtc1) {
		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem() && vrtc1 == a.getDestino()) {
				return;
			}
		}

		Aresta a = new Aresta();
		a.setOrigem(vrtc);
		a.setDestino(vrtc1);
		arestas.add(a);
		valorArestas.put(a, 0);

		for (Aresta as : arestasSimples) {
			if ((vrtc == as.getOrigem() && vrtc1 == as.getDestino())
					|| (vrtc == as.getDestino() && vrtc1 == as.getOrigem())) {
				return;
			}
		}

		arestasSimples.add(a);
	}

	@Override
	public void changeTipoGrafo(ETipoGrafo etg) {
		eTipoGrafo = etg;
	}

	@Override
	public void clickRemoveVertice(Vertice vrtc) {
		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem() || vrtc == a.getDestino()) {
				arestas.remove(a);
				valorArestas.remove(a);
				if (arestasSimples.contains(a)) {
					arestasSimples.remove(a);
				}
			}
		}
		vertices.remove(vrtc);
	}

	@Override
	public int temCaminho(Vertice vrtc, Vertice vrtc1) {
		ArrayList<Aresta> origens = new ArrayList<Aresta>();

		if (eTipoGrafo == ETipoGrafo.Orientado) {
			return buscaCaminho(vrtc, vrtc1, origens);
		}

		return -1;
	}

	private int buscaCaminho(Vertice vrtc, Vertice vrtc1, ArrayList<Aresta> origens) {
		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem() && vrtc1 == a.getDestino()) {
				return 1;
			}
		}

		int caminho = 1;
		boolean loopInfinito;

		for (Aresta a : arestas) {
			loopInfinito = false;
			if (vrtc == a.getOrigem()) {
				for (Aresta origem : origens) {
					if (a.getDestino() == origem.getOrigem())
						loopInfinito = true;
				}
			}

			if (vrtc == a.getOrigem() && vrtc != a.getDestino() && !loopInfinito) {
				origens.add(a);
				int buscaCaminho = buscaCaminho(a.getDestino(), vrtc1, origens);

				if (buscaCaminho != -1) {
					caminho = caminho + buscaCaminho;
					return caminho;
				}
				origens.remove(a);
			}
		}
		return -1;
	}

	@Override
	public List<Vertice> verticesFontes() {
		List<Vertice> vrtcFontes = new ArrayList<Vertice>();

		for (Vertice v : vertices) {
			if (getGrauRecpcao(v) == 0 && getGrauEmissao(v) > 0) {
				vrtcFontes.add(v);
			}
		}

		return vrtcFontes;
	}

	@Override
	public List<Vertice> verticesSumidouros() {
		List<Vertice> vrtcSumidouros = new ArrayList<Vertice>();

		for (Vertice v : vertices) {
			if (getGrauRecpcao(v) > 0 && getGrauEmissao(v) == 0) {
				vrtcSumidouros.add(v);
			}
		}

		return vrtcSumidouros;
	}

	@Override
	public boolean ehBipartido() {
		for (Aresta a : arestas) {
			if (a.getOrigem() == a.getDestino()) {
				return false;
			}
		}

		List<Vertice> partidoA = new ArrayList<Vertice>();
		List<Vertice> partidoB = new ArrayList<Vertice>();

		for (Vertice v : vertices) {
			if (!partidoA.contains(v) && !partidoB.contains(v)) {
				partidoA.add(v);
				separar(v, partidoA, partidoB);
			}
		}

		return Collections.disjoint(partidoA, partidoB);
	}

	@Override
	public boolean ehBipartidoCompleto() {
		if (ehBipartido()) {
			for (Vertice v : vertices) {
				for (Vertice v2 : vertices) {
					if (getGrau(v) != getGrau(v2)) {
						return false;
					}
				}
			}
		} else {
			return false;
		}

		return true;
	}

	private void separar(Vertice v, List<Vertice> partidoA, List<Vertice> partidoB) {
		if (partidoB.contains(v)) {
			for (Aresta a : arestas) {
				if (v == a.getOrigem()) {
					if (!partidoA.contains(a.getDestino())) {
						partidoA.add(a.getDestino());
						separar(a.getDestino(), partidoA, partidoB);
					}
				}
				if (v == a.getDestino()) {
					if (!partidoA.contains(a.getOrigem())) {
						partidoA.add(a.getOrigem());
						separar(a.getOrigem(), partidoA, partidoB);
					}
				}
			}
		} else {
			for (Aresta a : arestas) {
				if (v == a.getOrigem()) {
					if (!partidoB.contains(a.getDestino())) {
						partidoB.add(a.getDestino());
						separar(a.getDestino(), partidoA, partidoB);
					}
				}
				if (v == a.getDestino()) {
					if (!partidoB.contains(a.getDestino())) {
						partidoB.add(a.getOrigem());
						separar(a.getOrigem(), partidoA, partidoB);
					}
				}
			}
		}
	}

	@Override
	public boolean ehCompleto() {

		if (eTipoGrafo == ETipoGrafo.NaoOrientado && arestas.size() == arestasSimples.size()) {
			for (Aresta a : arestas) {
				if (a.getOrigem() == a.getDestino()) {
					return false;
				}
			}

			if (((vertices.size() * (vertices.size() - 1)) / 2) == arestasSimples.size()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean ehRegular() {
		if (eTipoGrafo == ETipoGrafo.NaoOrientado) {
			for (Vertice v : vertices) {
				if (getGrau(vertices.get(0)) != getGrau(v)) {
					return false;
				}
			}
		} else {
			for (Vertice v : vertices) {
				if (getGrau(vertices.get(0)) != getGrau(v) || getGrauEmissao(v) != getGrauRecpcao(v)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int temCadeia(Vertice vrtc, Vertice vrtc1) {
		ArrayList<Aresta> origens = new ArrayList<Aresta>();

		return buscaCadeia(vrtc, vrtc1, origens);
	}

	private int buscaCadeia(Vertice vrtc, Vertice vrtc1, ArrayList<Aresta> origens) {
		for (Aresta a : arestas) {
			if ((vrtc == a.getOrigem() && vrtc1 == a.getDestino())
					|| (vrtc == a.getDestino() && vrtc1 == a.getOrigem())) {
				return 1;
			}
		}

		int caminho = 1;
		boolean loopInfinito;

		for (Aresta a : arestas) {
			loopInfinito = false;
			if (vrtc == a.getOrigem() || vrtc == a.getDestino()) {
				for (Aresta origem : origens) {
					if ((vrtc == a.getOrigem())
							&& (a.getDestino() == origem.getOrigem() || a.getDestino() == origem.getDestino()))
						loopInfinito = true;

					if ((vrtc == a.getDestino())
							&& (a.getOrigem() == origem.getOrigem() || a.getOrigem() == origem.getDestino()))
						loopInfinito = true;
				}
			}

			if (vrtc == a.getOrigem() && vrtc != a.getDestino() && !loopInfinito) {
				origens.add(a);
				int busca = buscaCadeia(a.getDestino(), vrtc1, origens);

				if (busca != -1) {
					caminho = caminho + busca;
					return caminho;
				}
				origens.remove(a);
			}

			if (vrtc == a.getDestino() && vrtc != a.getOrigem() && !loopInfinito) {
				origens.add(a);
				int busca = buscaCadeia(a.getOrigem(), vrtc1, origens);

				if (busca != -1) {
					caminho = caminho + busca;
					return caminho;
				}
				origens.remove(a);
			}
		}
		return -1;
	}

	@Override
	public boolean ehCadeiaElementar(Vertice vrtc, Vertice vrtc1) {
		if (vrtc == vrtc1) {
			return false;
		}
		ArrayList<Aresta> origens = new ArrayList<Aresta>();
		return buscaCadeiaElementar(vrtc, vrtc1, origens);
	}

	private boolean buscaCadeiaElementar(Vertice vrtc, Vertice vrtc1, ArrayList<Aresta> origens) {
		for (Aresta a : arestas) {
			if ((vrtc == a.getOrigem() && vrtc1 == a.getDestino())
					|| (vrtc == a.getDestino() && vrtc1 == a.getOrigem())) {
				return true;
			}
		}

		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem() || vrtc == a.getDestino()) {
				for (Aresta origem : origens) {
					if ((vrtc == a.getOrigem())
							&& (a.getDestino() == origem.getOrigem() || a.getDestino() == origem.getDestino()))
						return false;

					if ((vrtc == a.getDestino())
							&& (a.getOrigem() == origem.getOrigem() || a.getOrigem() == origem.getDestino()))
						return false;
				}
			}

			if (vrtc == a.getOrigem() && vrtc == a.getDestino()) {
				return false;
			}

			if (vrtc == a.getOrigem() || vrtc == a.getDestino()) {
				origens.add(a);

				if (buscaCadeiaElementar(a.getDestino(), vrtc1, origens)) {
					return true;
				}
				origens.remove(a);
			}
		}
		return false;
	}

	@Override
	public boolean ehCadeiaSimples(Vertice vrtc, Vertice vrtc1) {
		if (vrtc == vrtc1) {
			return false;
		}
		ArrayList<Aresta> caminho = new ArrayList<Aresta>();
		return buscaCadeiaSimples(vrtc, vrtc1, caminho);
	}

	private boolean buscaCadeiaSimples(Vertice vrtc, Vertice vrtc1, ArrayList<Aresta> caminho) {
		for (Aresta a : arestas) {
			if ((vrtc == a.getOrigem() && vrtc1 == a.getDestino())
					|| (vrtc == a.getDestino() && vrtc1 == a.getOrigem())) {
				return true;
			}
		}

		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem() || vrtc == a.getDestino()) {
				for (Aresta aresta : caminho) {
					if (a == aresta)
						return false;
				}
			}

			if (vrtc == a.getOrigem() && vrtc == a.getDestino()) {
				return false;
			}

			if (vrtc == a.getOrigem() || vrtc == a.getDestino()) {
				caminho.add(a);

				if (buscaCadeiaSimples(a.getDestino(), vrtc1, caminho)) {
					return true;
				}
				caminho.remove(a);
			}
		}
		return false;
	}

	@Override
	public void armazenarValorAresta(Vertice vrtc, Vertice vrtc1, int valor) {
		for (Aresta a : arestas) {
			if (vrtc == a.getOrigem() && vrtc1 == a.getDestino()) {
				valorArestas.put(a, valor);
			}
		}
	}

	@Override
	public String geraDOT() {
		String dot = "";
		for (Vertice v : vertices) {
			for (Aresta a : arestas) {
				if (v == a.getOrigem()) {
					int valor = valorArestas.get(a);

					if (eTipoGrafo == ETipoGrafo.NaoOrientado) {
						dot += v.getNome() + " -- " + a.getDestino().getNome() + "[label=\"" + valor + "\",weight=\""
								+ valor + "\"];\n";
					} else if (eTipoGrafo == ETipoGrafo.Orientado) {
						dot += v.getNome() + " -> " + a.getDestino().getNome() + "[label=\"" + valor + "\",weight=\""
								+ valor + "\"];\n";
					}
				}
			}
		}

		if (eTipoGrafo == ETipoGrafo.NaoOrientado)
			return "graph G{" + dot + "}";
		else
			return "digraph G{" + dot + "}";
	}

	@Override
	public String arvoreGeradoraToDOT() {
		if (eTipoGrafo == ETipoGrafo.Orientado) {
			return "graph G{}";
		}

		ArrayList<Vertice> arvoreVertices = new ArrayList<Vertice>();
		ArrayList<Aresta> arvoreArestas = new ArrayList<Aresta>();

		HashMap<Aresta, Integer> valoresArestasCrescente = valorArestas.entrySet().stream()
				.sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		for (Aresta a : valoresArestasCrescente.keySet()) {
			if (!arvoreVertices.contains(a.getOrigem()) || !arvoreVertices.contains(a.getDestino())) {
				arvoreArestas.add(a);
			}
			if (!arvoreVertices.contains(a.getOrigem())) {
				arvoreVertices.add(a.getOrigem());
			}
			if (!arvoreVertices.contains(a.getDestino())) {
				arvoreVertices.add(a.getDestino());
			}
		}

		String arvoreGeradora = "";

		for (Aresta a : arestas) {
			if (arvoreArestas.contains(a)) {
				arvoreGeradora = arvoreGeradora.concat(
						a.getOrigem().getNome() + " -- " + a.getDestino().getNome() + " [color=red,penwidth=3.0];\n");
			} else {
				arvoreGeradora = arvoreGeradora
						.concat(a.getOrigem().getNome() + " -- " + a.getDestino().getNome() + ";\n");
			}
		}

		return "graph G{" + arvoreGeradora + "}";
	}

	@Override
	public String buscaLarguraToDTO(Vertice vrtc, Vertice vrtc1) {
		ArrayList<Aresta> caminho = new ArrayList<Aresta>();
		ArrayList<Vertice> visitados = new ArrayList<Vertice>();
		ArrayList<Vertice> fila = new ArrayList<Vertice>();
		visitados.add(vrtc);

		String buscaLargura = buscaLargura(vrtc, vrtc1, visitados, fila, caminho);
		if (!buscaLargura.isEmpty())
			buscaLargura.concat(";");

		if (eTipoGrafo == ETipoGrafo.NaoOrientado)
			return "graph G{" + buscaLargura + "}";
		else
			return "digraph G{" + buscaLargura + "}";
	}

	@Override
	public String buscaLarguraToString(Vertice vrtc, Vertice vrtc1) {
		ArrayList<Aresta> caminho = new ArrayList<Aresta>();
		ArrayList<Vertice> visitados = new ArrayList<Vertice>();
		ArrayList<Vertice> fila = new ArrayList<Vertice>();
		visitados.add(vrtc);

		String buscaLargura = buscaLargura(vrtc, vrtc1, visitados, fila, caminho);

		if (buscaLargura.isEmpty())
			return "Caminho não encontrado";

		return buscaLargura;
	}

	private String buscaLargura(Vertice vrtc, Vertice vrtc1, ArrayList<Vertice> visitados, ArrayList<Vertice> fila,
			ArrayList<Aresta> caminho) {

		String buscaLargura = "";

		for (Aresta a : arestas) {
			if (eTipoGrafo == ETipoGrafo.Orientado) {
				if (vrtc == a.getOrigem() && !visitados.contains(a.getDestino())) {
					caminho.add(a);
					fila.add(a.getDestino());
					visitados.add(a.getDestino());
				}
				if (vrtc == a.getOrigem() && vrtc1 == a.getDestino()) {
					return vrtc.getNome() + " -> " + vrtc1.getNome();
				}
			} else {
				if (vrtc == a.getOrigem() && !visitados.contains(a.getDestino())) {
					caminho.add(a);
					fila.add(a.getDestino());
					visitados.add(a.getDestino());
				} else if (vrtc == a.getDestino() && !visitados.contains(a.getOrigem())) {
					caminho.add(a);
					fila.add(a.getOrigem());
					visitados.add(a.getOrigem());
				}
				if ((vrtc == a.getOrigem() && vrtc1 == a.getDestino())
						|| (vrtc1 == a.getOrigem() && vrtc == a.getDestino())) {
					return vrtc.getNome() + " -- " + vrtc1.getNome();
				}
			}
		}

		if (!fila.isEmpty()) {
			buscaLargura = buscaLargura(fila.remove(0), vrtc1, visitados, fila, caminho);

			for (Aresta a : caminho) {
				if (eTipoGrafo == ETipoGrafo.Orientado) {
					if (vrtc == a.getOrigem() && buscaLargura.startsWith(a.getDestino().getNome())) {
						return vrtc.getNome() + " -> " + buscaLargura;
					}
				} else {
					if ((vrtc == a.getOrigem() && buscaLargura.startsWith(a.getDestino().getNome()))
							|| (vrtc == a.getDestino() && buscaLargura.startsWith(a.getOrigem().getNome()))) {
						return vrtc.getNome() + " -- " + buscaLargura;
					}
				}
			}
		}

		return buscaLargura;
	}

	@Override
	public String buscaProfundidadeToDTO(Vertice vrtc, Vertice vrtc1) {
		ArrayList<Aresta> origens = new ArrayList<Aresta>();

		String buscaProfundidade = buscaProfundidade(vrtc, vrtc1, origens);
		if (!buscaProfundidade.isEmpty())
			buscaProfundidade.concat(";");

		if (eTipoGrafo == ETipoGrafo.NaoOrientado)
			return "graph G{" + buscaProfundidade + "}";
		else
			return "digraph G{" + buscaProfundidade + "}";
	}

	@Override
	public String buscaProfundidadeToString(Vertice vrtc, Vertice vrtc1) {
		ArrayList<Aresta> origens = new ArrayList<Aresta>();

		String buscaProfundidade = buscaProfundidade(vrtc, vrtc1, origens);

		if (buscaProfundidade.isEmpty())
			return "Caminho não encontrado";

		return buscaProfundidade;
	}

	private String buscaProfundidade(Vertice vrtc, Vertice vrtc1, ArrayList<Aresta> origens) {
		for (Aresta a : arestas) {
			if (eTipoGrafo == ETipoGrafo.Orientado && vrtc == a.getOrigem() && vrtc1 == a.getDestino()) {
				return vrtc.getNome() + " -> " + vrtc1.getNome();
			} else if (eTipoGrafo == ETipoGrafo.NaoOrientado && ((vrtc == a.getOrigem() && vrtc1 == a.getDestino())
					|| (vrtc == a.getDestino() && vrtc1 == a.getOrigem()))) {
				return vrtc.getNome() + " -- " + vrtc1.getNome();
			}
		}

		String caminho = "";
		boolean loopInfinito;

		for (Aresta a : arestas) {
			loopInfinito = false;
			if (eTipoGrafo == ETipoGrafo.Orientado && vrtc == a.getOrigem()) {
				for (Aresta origem : origens) {
					if (a.getDestino() == origem.getOrigem())
						loopInfinito = true;
				}
			} else if (eTipoGrafo == ETipoGrafo.NaoOrientado) {
				if (vrtc == a.getOrigem()) {
					for (Aresta origem : origens) {
						if (a.getDestino() == origem.getOrigem() || a.getDestino() == origem.getDestino())
							loopInfinito = true;
					}
				} else if (vrtc == a.getDestino()) {
					for (Aresta origem : origens) {
						if (a.getOrigem() == origem.getOrigem() || a.getOrigem() == origem.getDestino())
							loopInfinito = true;
					}
				}
			}

			if (vrtc == a.getOrigem() && vrtc != a.getDestino() && !loopInfinito) {
				origens.add(a);
				String buscaProfundidade = buscaProfundidade(a.getDestino(), vrtc1, origens);

				if (!buscaProfundidade.equals("")) {
					if (eTipoGrafo == ETipoGrafo.NaoOrientado)
						caminho += vrtc.getNome() + " -- " + buscaProfundidade;
					else
						caminho += vrtc.getNome() + " -> " + buscaProfundidade;

					return caminho;
				}
				origens.remove(a);
			} else if (eTipoGrafo == ETipoGrafo.NaoOrientado && vrtc == a.getDestino() && vrtc != a.getOrigem()
					&& !loopInfinito) {
				origens.add(a);
				String buscaProfundidade = buscaProfundidade(a.getOrigem(), vrtc1, origens);

				if (!buscaProfundidade.equals("")) {
					caminho += vrtc.getNome() + " -- " + buscaProfundidade;
					return caminho;
				}
				origens.remove(a);
			}
		}
		return "";
	}

	public int verificarVisita(Vertice vertc) {
		if (Visitado.get(vertc) == true) {
			return 1;
		}
		return -1;
	}

	@Override
	public String dijkstraToString(Vertice vrtc, Vertice vrtc1) {

		List<Vertice> naoVisitados = new ArrayList<Vertice>();
		Vertice verticeCaminho;
		Vertice atual;
		Vertice vizinho;
		Map<Vertice, Integer> distancia = new HashMap<Vertice, Integer>();

		menorCaminho.add(vrtc);
		for (Vertice v : vertices) {
			if (v.equals(vrtc)) {
				distancia.put(v, 0);
				Visitado.put(v, true);
			} else {
				distancia.put(v, 9999);
				Visitado.put(v, false);
			}
			naoVisitados.add(v);
		}

		while (!naoVisitados.isEmpty()) {
			atual = naoVisitados.get(0);

			for (Aresta a : arestas) {

				if (atual == a.getOrigem()) {

					vizinho = a.getDestino();

					if (verificarVisita(vizinho) == -1) {

						if (distancia.get(vizinho) > distancia.get(atual) + valorArestas.get(a)) {

							int result = distancia.get(atual) + valorArestas.get(a);
							distancia.put(vizinho, result);
							Pai.put(vizinho, atual);

							if (vizinho.equals(vrtc1)) {
								menorCaminho.clear();
								verticeCaminho = vizinho;
								menorCaminho.add(vizinho);
								while (Pai.get(verticeCaminho) != null) {
									menorCaminho.add(Pai.get(verticeCaminho));
									verticeCaminho = Pai.get(verticeCaminho);
								}
							}
						}
					}
				}
			}

			Visitado.put(atual, true);
			naoVisitados.remove(atual);
		}

		String concatenar = "";
		Collections.reverse(menorCaminho);
		if (eTipoGrafo == ETipoGrafo.Orientado) {
			for (Vertice v : menorCaminho) {
				if (concatenar.isEmpty()) {
					concatenar = v.getNome();
				} else {
					concatenar = concatenar + " -> " + v.getNome();
				}
			}
			return concatenar;
		} else {
			for (Vertice v : menorCaminho) {
				if (concatenar.isEmpty()) {
					concatenar = v.getNome();
				} else {
					concatenar = concatenar + " -- " + v.getNome();
				}
			}
			return concatenar;
		}

	}

	@Override
	public String dijkstraToDTO(Vertice vrtc, Vertice vrtc1) {
		String dot = "";
		for (Vertice v : menorCaminho) {
			for (Aresta a : arestas) {
				if (v == a.getDestino() && a.getOrigem() == Pai.get(v)) {
					int valor = valorArestas.get(a);

					if (eTipoGrafo == ETipoGrafo.NaoOrientado) {
						dot += a.getOrigem().getNome() + " -- " + v.getNome() + "[label=\"" + valor + "\",weight=\""
								+ valor + "\"];\n";
					} else if (eTipoGrafo == ETipoGrafo.Orientado) {
						dot += a.getOrigem().getNome() + " -> " + v.getNome() + "[label=\"" + valor + "\",weight=\""
								+ valor + "\"];\n";
					}
				}
			}
		}

		if (eTipoGrafo == ETipoGrafo.NaoOrientado) {
			return "graph G{" + dot + "}";
		} else {
			return "digraph G{" + dot + "}";
		}
	}

	@Override
	public String grafoReduzidoToDOT() {

		List<Vertice> copiaVertices = new ArrayList<Vertice>(vertices);
		List<List<Vertice>> intersecoes = new ArrayList<List<Vertice>>();

		while (!copiaVertices.isEmpty()) {
			intersecoes.add(melgrange(copiaVertices));
		}

		String dot = "";
		for (List<Vertice> l : intersecoes) {
			for (Vertice v : l) {
				for (Aresta a : arestas) {
					if (v == a.getOrigem() && l.contains(a.getDestino())) {
						int valor = valorArestas.get(a);
						
						dot += v.getNome() + " -> " + a.getDestino().getNome() + "[label=\"" + valor + "\",weight=\""
								+ valor + "\"];\n";
					}else if(v == a.getOrigem()) {
						dot += v.getNome() + ";\n";
					}
				}
			}
		}
		return "digraph G{" + dot + "}";
	}

	@Override
	public String grafoReduzidoToString() {
		List<Vertice> copiaVertices = new ArrayList<Vertice>(vertices);
		List<List<Vertice>> intersecoes = new ArrayList<List<Vertice>>();

		while (!copiaVertices.isEmpty()) {
			intersecoes.add(melgrange(copiaVertices));
		}

		String grafo = "";

		for (List<Vertice> l : intersecoes) {
			for (Vertice v : l) {
				grafo = grafo + v.getNome();
			}

			grafo = grafo + "\n";
		}

		return grafo;
	}

	public List<Vertice> melgrange(List<Vertice> copiaVertices) {
		List<Vertice> visitadosA = new ArrayList<Vertice>();
		List<Vertice> visitadosB = new ArrayList<Vertice>();
		List<Vertice> intersecao = new ArrayList<Vertice>();
		visitadosA.add(copiaVertices.get(0));
		visitadosB.add(copiaVertices.get(0));
		for (Vertice v : copiaVertices) {
			for (Aresta a : arestas) {
				if (v == a.getOrigem() && visitadosA.contains(a.getOrigem()) && !visitadosA.contains(a.getDestino())) {
					visitadosA.add(a.getDestino());
				}
			}
		}
		for (Vertice v : copiaVertices) {
			for (Aresta a : arestas) {
				if (v == a.getDestino() && visitadosB.contains(a.getDestino()) && !visitadosB.contains(a.getOrigem())) {
					visitadosB.add(a.getOrigem());
				}
			}
		}
		for (Vertice v : visitadosA) {
			if (visitadosB.contains(v)) {
				intersecao.add(v);
				copiaVertices.remove(v);
			}
		}

		return intersecao;
	}
}
