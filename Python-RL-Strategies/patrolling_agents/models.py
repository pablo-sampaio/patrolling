
import numpy as np

import torch
import torch.nn as nn
import torch.optim as optim 

class BaseAgentModel(nn.Module):
    def __init__(self):
        super().__init__()

    def optimizer_zero_grad(self):
        pass

    def optimizer_step(self):
        pass

    def clone(self):
        pass


class LinearModel(BaseAgentModel):
    def __init__(self, num_features, lr):
        super().__init__()
        # generate random numbers in range [0;1)
        w = torch.rand(size=(1, num_features, 1))

        # xavier weight initialization
        # range for the weights
        lower, upper = -(1.0 / np.sqrt(num_features)), (1.0 / np.sqrt(num_features))
        # scale to the desired range
        w = lower + w * (upper - lower)
        self.weights = nn.Parameter(w)
        self.lr = lr
        self.optimizer = optim.Adam([self.weights], lr=self.lr)

    def forward(self, x):
        # input shape: (#AGENTS, #FEATURES, #NODES)
        # como uma convolucao 1D na dimensão das features, por agente
        y = (x * self.weights).sum(dim=1)  # condensa a dimensao das features

        # output shape: (#AGENTS, #NODES)
        return y

    def optimizer_zero_grad(self):
        self.optimizer.zero_grad() 

    def optimizer_step(self):
        self.optimizer.step()
    
    def clone(self):
        cloned = LinearModel(self.weights.shape[1], self.lr)
        cloned.load_state_dict(self.state_dict()) # só copia os pesos do atributo 'weights'
        return cloned


class MlpModel(BaseAgentModel):
    def __init__(self, num_features, list_hidden_dims, lr):
        super().__init__()
        layers = []

        first_layer = True
        last_dim = num_features
        for dim in list_hidden_dims:
            layers.append( nn.Linear(last_dim, dim, bias=not first_layer) ) # the bias for the first layer is a 'feature' in the 
            layers.append( nn.ReLU() )
            first_layer = False
            last_dim = dim
        layers.append( nn.Linear(last_dim, 1, bias=not first_layer) )
        self.layers = nn.Sequential(*layers)
        
        self.num_features = num_features
        self.list_hidden_dims = tuple(list_hidden_dims)
        self.lr = lr
        self.optimizer = optim.Adam(self.parameters(), lr=self.lr)

    def forward(self, x : torch.tensor):
        # input shape: (#AGENTS, #FEATURES, #NODES)
        
        # troca a ordem para (#AGENTS, #NODES, #FEATURES)
        # TODO: change the order in the wrapper, then remove the line below
        x = x.transpose(1, 2)
        
        # reshape to (#AGENTS * #NODES, #FEATURES)
        #y = x.view(-1, x.shape[2]) 
        y = x.reshape(-1, x.shape[2]) 
        
        # Aplica as camadas
        y = self.layers(y)
        
        # reshape to (#AGENTS, #NODES)
        y = y.reshape(x.shape[0], x.shape[1])
        
        return y

    def optimizer_zero_grad(self):
        self.optimizer.zero_grad() 

    def optimizer_step(self):
        self.optimizer.step()

    def clone(self):
        cloned = MlpModel(self.num_features, self.list_hidden_dims, self.lr)
        cloned.layers.load_state_dict(self.layers.state_dict())
        return cloned


class SetOfAgentsModels(nn.Module):
    def __init__(self, num_agents, example_model : BaseAgentModel):
        super().__init__()
        self.models = nn.ModuleList()
        for i in range(num_agents):
            self.models.append(example_model.clone())

    def forward(self, x : torch.tensor):
        # input shape: same supported by the 'example_model'
        
        ylist = []
        for ag_i, ag_model in enumerate(self.models):
           agent_input = x[ag_i:ag_i+1]
           ylist.append( ag_model(agent_input) )
        
        # same as the 'example_model', i.e. (#AGENTS, #NODES)
        return torch.cat(ylist)

    def optimizer_zero_grad(self):
        for ag_model in self.models:
            ag_model.optimizer_zero_grad() 

    def optimizer_step(self):
        for ag_model in self.models:
            ag_model.optimizer_step()

