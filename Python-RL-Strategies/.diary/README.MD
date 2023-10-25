# TODO

- Prioridades
  - outros agentes de tempo real
  - analisar as normalizações do wrapper!
  - fazer valores relativos ao policy_lr?
  - cogitar tratar grafos com pesos
  - parametrizar para permitir normalizar ou não cada feature, separadamente
  - comparar o agente depois de treinado, com a métrica real / usar política determinística?
  - fazer agente aleatório, de baseline
  - comparar com estratégias locais da patrulha!!

- FOCAR NO ARTIGO!!
- fazer treinamentos (reiniciar) para grafos com diferentes características topológicas e comparar como ficam os pesos nos diferentes treinamentos (dificuldade: encontrar conjunto de grafos de tamanho pequeno com variabilidade nas características topológicas)
- refactoring
  - fazer refactoring nas pastas de experimentos e otimização (unificar?)
  - refactoring dos nomes dos agentes?
  - melhorar ferramenta de comparação antiga -- remover??
- fazer essa mesma bateria de testes e otimização com o agente rl_agent_1
- com ELIG. TRACE - versão 3?
- deixar recompensas (e parâmetros de aprendizagem) independente do número de agentes?
- pensar nessa questão: estamos usando recompensa única para o time; poderia ser individual? só no caso 'visited'?
- pensar em deixar as features com profundidade n 
  - ver ideias em nota no Keep para profundidade 2
  - prof n: calcular a métrica de prof n-1 para os vizinhos e, depois, combinar
- outras normalizações: presence - dividir pelo número de agentes? ou: não normalizar?


# Diário de Desenvolvimento

## 24/10/2023
- Em 24/10: criei o ambiente para grafos com pesos! 
  - setei action e observation spaces corretamente (o outro ambiente não tem!)
  - testei, com sucesso
- TODO: criar wrappers (ou antes: ajustar as normalizações do wrapper anterior?)

## Até 30/set/2023
- Em 12/09: implementações
  - criei funções para simular agente treinado e receber as listas de nós visitados
  - fiz notebook legal para analisar os dados retornados
- Em 12/09: experimentos preliminares variando as métricas, o `shared_weights`, com features=`pidcq`, analisando resultados de frequências de visita por nós
- Em geral, a convergência é mais lento quando não usa shared weights
- Com `visited_idleness`: treinamento muito rápido, pouco interessante
  - pesos dos agentes foram similares
  - visitação igualitária mesmo sem shared weights
- Com `avg_idleness`: gráfico de treinamento interessante (convergência por volta de 8k sem shared)
- Com `visited_quadr_idleness`: converge bem mais lentamente (usei 10k passos)
- Nas duas métricas acima, principalmente na última (`visited_quadr`), sem `shared_weights` (ou seja, com pesos individuais)
  - agentes se especializaram (um pouco) em diferentes regiões
  - pesos treinados variaram entre os agentes -> pesos de sinais contrários para 'presence' (várias vezes) e outras features (com menos frequência)
  - aconteceu um pouco com shared weights também (?)
- Em 13/09: fiz cálculo das métricas / agente random
  - experimentos (pós-treinamento) dando valores de métricas muito similares mesmo variando: tipo de recompensa e se compartilha (ou não) pesos
  - variei também se a escolha é determinística ou não: resultados deram uniformes dentro de cada opção dessas
  - criei o agente random e (felizmente) deu resultado bem inferior ao rl_agent_2
- Em 14/09: experiências com MLP sem compartilhar
  - resultado bem legal com métrica 'delta_avg_idleness': agentes com frequências de visitação bem distintas!


## Até 10/set/2023

- Implementei as novas ideias planejadas
  - visitas por ID de agente (agente x soma dos outros)
  - pesos individuais por agente (sem compartilhar)
  - redes neurais multi-camadas
- Também finalizei a revisão das métricas
  - algumas foram removidas, outras renomeadas
  - fiz alguns comentários sobre as métricas (com base em análise intuitiva)
- Fiz o "critic_lr" ser definido por um valor relativo em função do "policy_lr"

## Até 20/jun/2023
- Esta semana, investi na ideia de implementar um Differential Q-Learning e seu análogo n-step SARSA como métodos tabulares
- Fiz o rl_agent_3 com uma implementação mais simples
  - tive problema com explosão de estados
  - não vi convergir mesmo para ?? passos...
  - em média da recompensa durante todo o treinamento é -300 (oscilando em torno desse valor)
- Fiz o rl_agent_3x com uma implementação mais elaborada, em que os estados são considerados iguais se os valores são os mesmos a menos da ordem lexicográfica dos vizinhos
  - essa implementação ainda sofre com a explosão dos estados
  - e ficou bem mais lenta!
- Somente parece haver convergência moderada quando uso apenas 'p' ou 'pd' como features (mas só elas tornam difícil chegar a uma boa política)
  - quantidade de estados fica bem limitada (830, no caso 'pd', após 1M passos)
  - desempenho ao longo do treinamento em torno de -100 em ambos os casos
- Conclusão: **é inviável implementar com métodos tabulares**!

## Até 08/mai/2023 - resumo
- Os updates "2_" são bons (na patrulha e outros ambientes), mas são difíceis de justificar teoricamente
- A análise que tenho feito, variando apenas o beta_update e deixando os outros parâmetros inalterados não é ideal
  * fazer um grid search e pegar a melhor parametrização por beta_update!
- Os updates 41 e 42 são os que eu penso em investir
- Primeiro, vou pensar em um artigo na patrulha propondo o 41 e 42, mas o foco é a patrulha apenas!
- Depois, posso tentar outro artigo mais geral propondo avaliar todas as outras variantes
  * comparar cada um na sua melhor parametrização
  * dizer que o "2_" são mais complicados teoricamente, mas foram testados como análogos do "4_" e tiveram resultados interessantes na prática

## Até 06/mai/2023
- Sobre a implementação das métricas: tive dúvida se estavam corretas, então revisei-as
- Considerações sobre a modelagem do problema em geral (comparando, por exemplo, com o trabalho de Hugo)
  - o mecanismo de decisão (e os pesos) é compartilhado entre os agentes e o valor da recompensa é coletivo dos agentes
  - isso é um passo atrás em relação ao trabalho de Hugo
  - fica como passo futuro melhorar essa questão
  - qual a contribuição? as features diferentes que foram usadas (mesclagem com real-time) / espero que gere desempenho melhor que elas sozinhas
- Criei nova métrica 'visited_...' e testei em vários experimentos com Optuna
  - 'visited_idleness': usada no trabalho de Hugo, é positiva, e calculada apenas nos nós atingidos pelos agentes
  - 'visited_'quadratic_interval': semelhante, mas eleva os valores ao quadrado; resultados esquisitos, que parecem favorecer os parâmetros que demoram a aprender (assim ele 
demora a chegar em alguns nós e, ao chegar, recebe uma recompensa "explosiva")
  - fiz essas métricas considerarem apenas nós "únicos" visitados (assim, só conta uma vez quando dois agentes visitam um certo nó) (Hugo nada comenta sobre isso))
- Criei uma feature de presença no nó atual
  - indica se tem mais de 1 agente no mesmo nó de origem 
  - se tiver, ela é setada como 1 nas features de todos os vizinhos
- Próximo passo: comparar avg_idleness (verdadeira) com visited_idleness (considerando nós únicos visitados)
  - visited_idleness: rodar optuna, rodar script de beta_update
  - anotar impressões aqui !!!

- Investigação paralela com OUTROS ambientes: como os diferentes beta-updates impactam o algoritmo (de forma geral)? 
  Será que essas variações (do original) valem a pena ser estudadas?
- Analisei CartPole, e é similar à patrulha: o beta_update ideal depende da quantidade de passos 
  - o 21 é melhor no agregado e disputa com 41 e 42
  - o beta_update=42 foi a melhor configuração, junto com nstep=2
  - o agente treinado fica muito bom, tanto estocástico como determinístico
- Analisando o Acrobot
  - aprende super bem também
  - a melhor hiper-parametrização usa beta_update 21, mas 37 e 38 são bons também
- TODO:
  - adicionar parâmetro para escolher normalizar ou não as features (em geral)
  - parametrizar para permitir normalizar ou não cada feature
  - o idleness/intervalo deveria permitir alongar tanto quanto se queira, para "puxar" as ações para ele
  - debugar agente: ver nós visitados, etc -> criar método para exportar stats para .csv

## Até 19/abr/2023
- O Inov.EDU me consumiu, só agora consegui retomar, visando produzir um artigo em breve
- Em 12/04: fiz uma nova feature, que é a primeira de caráter "topológico": grau (quantidade de vizinhos) de um nó, também chamada de medida *degree centrality* (d)
- Em 13/04: 
  - adicionei outra feature topológica: *closeness centrality* (K)
  - resultados ruins com as novas features topológicas (nas métricas usadas até então: avg_idleness e delta_avg_idleness)
  - **suspeita 1**: a feature 'i' (idleness) está diretamente ligada ao cálculo da recompensa; por isso, ela domina os resultados (junto com a feature 'p' - presença)
  - **suspeita 2**: o crítico tem pouca informação para usar na avaliação -- é um POMDP
  - resolvi mexer nas métricas
  - adicionei métrica delta_max_idleness (já havia max_idleness) e quadratic_interval - tentando desacoplar a métrica do crítico um pouco
- Experimentos comparando as métricas:
  - avg_idleness, max_idleness e versões delta delas -> 'pi' foi o melhor, e 'pidk' foi o pior; ou seja, features topológicas não auxiliam!
  - quadratic_interval -> resultado legal: 'pidk' melhor e 'pi' pior!
- Em 14/04: rodei os scripts de experimentos usando a métrica 'quadratic_interval'
  - houve algumas mudanças nos resultados (em qual parâmetro é melhor/pior), mas pouco importantes
  - o importante é que os resultados foram coerentes e permitem seguir com a métrica
- En 17/04: para melhor diferenciar entre os valores de beta_update, calculei e imprimi diversos indicadores
  - usei n-steps 1, 2, 4, 8 e 16
  - o melhor beta_update é '21' (normalized_sum=4.00, rank_sum=10.00)
  - o segundo melhor é o '42' (normalized_sum=3.92, rank_sum=14.00)
  - em terceiro, vem o '41' (normalized_sum=3.89, rank_sum=16.00)
- Em 18/04: 
  - fiz LR separadas para o crítico e a política
  - fiz uma otimização rápida (~120 trials) para estimar os melhores parâmetros (LR e features, em especial), resultado:
  ```
    "features"      : 'pid',
    "policy_lr"     : 0.165,
    "critic_lr"     : 0.079,
    "beta"          : 0.141,
    "beta_update"   : 21,
    "n_step"        : 24
  ```
  - experimentos para tentar refinar os parâmetros achados pelo Optuna; novos valores:
  ```
    "features"      : 'npid', (depois 'pild' e 'pid')
    "policy_lr"     : 0.2475,
    "critic_lr"     : 0.1185,
    "beta"          : 0.141,
    "beta_update"   : 21,
    "n_step"        : 24
  ```
- Trabalhando em um grid search. Na primeira versão, ia rodar por mais de um ano. Tive que fazer várias restrições:
  - features fixas: 'p' e 'i'
  - no máximo uma feature topológica ('d' ou 'k')
  - reduzi número de re-execuções do algoritmo para 4 (eram 30)
  - coloquei um corte (pruning)
  - usei apenas beta_update=21
  - reduzi o número de opções de valores para as taxas de aprendizado e beta
  - dúvidas sobre as métricas me fizeram parar...


## Até 07/jun/2022
- passei um tempo parado por conta do projeto INOV.EDU, retomando em 30/05/22
- carreguei os dados para pandas, para facilitar a análise


## Até 15/out/2021
- implementei bias no agente 2
  - melhorar usar de tensores! (Voltar a guardar)
- gastei um bom tempo testando e optimizando com MountainCar
  - tentei diferentes critérios de otimização
  - fiz um pouco de "reward shaping"
  - pouco sucesso!

## Até 01/out/2021
- fiz load/save do agente
- fiz experimentos variando nsteps e beta_update no Cartpole: resultados similares aos do agente de patrulha 2:
  - impressão geral: a parametrização está boa, com pouca margem para melhorar localmente
  - comparação entre versões com e sem V (23,43,44): todas muito iguais
  - comparações entre cálculo da média antes e depois de V: inconclusivo, não tem uma ordem melhor em geral
  - melhores em geral para nstep >= 4: updates 21 e 41 
  - para cada beta_update fixo, a quantidade de passos em que ele se sai melhor varia (isso difere da patrulha, em que todos os beta_update tem nstep=1 como melhor)
  - com poucos passos: há muita "igualdade", mas com resultados ruins em geral
- refiz otimização, usando a soma das últimas *recompensas* (não acumuladas), usando log_uniform; achei parâmetros *muito* bons:
```
    'beta': 0.008842037709363714, 
    'beta_update': 21, 
    'learning_rate': 0.02966896182775668, 
    'nsteps': 21
```
- otimizei com CartPole e achei parâmetros bons:
```
    'beta': 0.017513539389141228, 
    'beta_update': 41, 
    'learning_rate': 0.023748618019383216, 
    'nsteps': 14
```
- fiz um agente similar ao rl_agent_2 para rodar com ambientes do gym de ações discretas, para testar, inicialmente, com o Cartpole

## Até 28/set/2021
- usando a otimização abaixo como base, reanalisei os resultados com os beta_updates, variando o nstep, mas os resultados foram bem parecidos com os obtidos antes
  - 21, 41 e 42 melhores, no geral 
  - mas em nstep=2, 41 é péssimo; e 42 cai com nstep alto
- fiz nova otimização, em 120 trials, usando um critério de estabilidade (recompensa acumulada nos últimos 70 passos do treinamento), obtendo:
```
    'beta': 0.6230882544015833,
    'beta_update': 41,
    'features': 'pi',
    'learning_rate': 0.09008345175142912,
    'nsteps': 4
```
- fiz script para trocar os antigos valores de beta_update pelos novos (nos arquivos .npy)
- renomeei os ids (numéricos) de todos os beta_updates, mantendo os antigos
  - os que não tem "normalização" por n_steps não ganharam novos ids
- criei vários novos beta-updates
- fiz script para comparar cada update=x com seu correspondente (múltiplos gráficos por nstep)
- fiz graficos para comparar cada update=x entre os vários nsteps (1 grafico por beta)
  - update = 1 (novo 21) é o melhor (bom resultado em todos os nsteps)
  - update = 2 (novo 41) segue de perto, mas é terrível com nstep=2
  - update = 4 (novo 42) é razoavelmente bom, mas cai na proporção do nstep
- criei novos beta-updates de 7 a 9, correspondentes aos existentes, mas anteriores ao cálculo do V
  - resultados não muito animadores
  - mas dividir por n_step: 
    - se mostrou útil para melhorar a comparação com outros updates (para um mesmo beta); 
    - faz sentido, pois equivale a tirar a média das recompensas dos últimos n passos e depois subtrair a média
- gastei tempo criando investigando os valores de beta-update (para nsteps 2 e 3 e 4 e 8)
  - o 1, 2 e 4 (21/41/42) disputam as primeiras posições; mas o update=2 (41) foi o pior *geral* com nstep=2; os updates 1 e 2 parecem ser melhores (em relação ao 4) com nstep mais altos
    - sobre a diferença dos updates 1 e 2: no 1, a média é atualizada antes de ser usada na estimativa do estado; no 2, ela é atualizada depois
  - o novo update=6 junto com o update=3 parecem ser os piores, mas talvez por não se adequarem ao mesmo "beta" que os outros (comparei todos com um beta fixo)
  - o update=5 (novo 43) (com a divisão) tem sempre desempenho intermediário (entre o grupos dos piores e o dos melhores)
- criei e testei novo beta_update 6, similar ao 5 (novo 43), mas sem a divisão
- mudei o posicionamento no código do beta-update 5 (tirei do bloco de otimização)
  - resultados idênticos (como esperado)!
  - só o caso 3 permanece no mesmo lugar por conta da dependência do cálculo do V do estado
- melhorei a reproducibilidade, definindo as sementes do torch e do numpy
- refiz os experimentos, usando os novos valores quase-ótimos obtidos
  - beta-update: as curvas das opções 2, 4, 5 e 6 ficaram totalmente **empatadas**; depois de algum tempo investigando, entendi que é por conta do nstep=1 que faz todos usarem a última recompensa apenas
  - lr: o valor menor (metade) começa mais baixo, mas assume o topo no fim do treinamento
  - beta: o valor ótimo foi muito alto; a metade dele parece mais "estável"
  - nstep: ficou bem mais comportado; o 1 é o melhor, e quanto maior, pior
  - features: o npi é melhor no começo, mas, a longo prazo, todos parecem semelhantes, até o "i" sozinho! mas "n" sozinho é ruim -- não converge!

## Até 17/set/2021

Fiz:
- reset das features no wrapper do ambiente
- mudança no uso das sementes aleatórias
- redesign dos experimentos do agent 2 
  - tentei identificar/tratar resultados esquisitos anteriores
  - não parece ter sido uso da semente, mas limitações do método (para esse problema)
- resumo da bateria de testes variando cada parâmetro a partir dos parâmetros obtidos no Optuna
  - lr: valor menor (0.085) parece melhor
  - nsteps: nstep=1 é melhor do que o 6 dado pelo Optuna; e nstep=3 é bem pior, o que é estranho
  - beta-update: o 2 é de fato, o melhor; o 3 é terrível (obs.: com base em outros experimentos, o melhor beta update depende do n-steps)
  - beta: valor maior (0,95; 2x o do Optuna) tem resultado parecido, mas um pouco melhor
  - features: três melhores: (1) pi; (2) npi (mas não evolui tão bem); (3) npil (dado pelo Optuna, iguala o pi no final)
- criei um novo beta-update (5, novo 43) para deixar os resultados com diferentes "beta" mais independentes do valor do "n-steps"
  - resultados ruins, mas melhores do que a variante 3
- fiz a funcionalidade de experimentos permitir retomar (resume) ou rodar com mais valores
- depois das duas alterações acima, os resultados de alguns parâmetros variaram moderadamente em relação às observações feitas no resumo acima:
  - fiz um script para comparar (experiment_compare)
  - varia por parâmetro e valor: vários sãos iguais (na execução anterior e na mais recente), outros melhoraram, outros pioraram
  - conclusões parciais: diferenças podem ser atribuídas a variações aleatórias, mas é esquisito, porque acho que fiz os experimentos com mesmo algoritmo de números aleatórios e mesma semente
- refactoring geral: experimentos, ambientes, agentes, scripts de otimização
  - apenas alguns scripts podem estar com erro de import
  - deixei para ajustar quando necessário (fazer comando sys.path(...))
- script para "fix" campos dos dict dos arquivos de resultados
- fiz refactoring que afetou todo o código; coisas menos usadas podem não estar funcionando (por questões de import)
  - aparentemente, faltam apenas alguns scripts de otimização não usados
- fiz nova otimização com 140 trials (com correção de bugs no script de otimização e acrescentando beta_update=5)
```
beta	      0.797132019470258
beta_update	4
features	  npi
lr	        0.0731198716582106
nsteps	    1
```
- fiz plot_study nos novos experimentos para entender interdependências
  - beta_update=5 ficou melhor que beta_update=3 (que é similar)

