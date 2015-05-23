%	featureselection.m and this program selects the features and discretize them generating the testing.txt.
%	Copyright (C) 2014	Abhishek Singh Sambyal	email:abhishek.sambyal@gmail.com

%	featureselection.m is free software: you can redistribute it and/or modify
%	it under the terms of the GNU General Public License as published by
%	the Free Software Foundation, either version 3 of the License, or
%	(at your option) any later version.

%	This program is distributed in the hope that it will be useful,
%	but WITHOUT ANY WARRANTY; without even the implied warranty of
%	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%	GNU General Public License for more details.

%	You should have received a copy of the GNU General Public License
%	along with this program.  If not, see <http://www.gnu.org/licenses/>.





% Declarations and initializations
total_feature = 140;
total_image = 43;
cutpoint_db = zeros(total_feature, total_image);
feature_reduction = 0.83;
check = zeros(total_image,2);

% It has a structure like (no of cutpoints, feature number)
no_of_feature = zeros(total_feature,2);
for feature = 1:total_feature
    for image = 1:total_image
        check(image,1) = value{image,2}(feature);
        check(image,2) = value{image,3}(1);
    end
    check = sortrows(check);
    check = num2cell(check);

    
    %% Cut points determination
    max_size = size(check,1);
    j = zeros(1,max_size);
    count = 1;

    % From first to last value of an array
    % give value to every interval of class starting form 1.
    for i = 1:max_size-1
        % Checking the value of each feature and compare it to the next one
        % if the feature class is differnent both feature will get the
        % different interval value stored in a cut point array "j" and if the 
        % feature class is same, them the same interval values are given to
        % both features and stored in the array.
        if(check{i,2} ~= check{i+1,2})
            j(i) = count;
            count = count + 1;
            j(i+1) = count;
        else
            j(i) = count;
            j(i+1) = count;
        end
    end


    %% Cut point removal by condition 1 (MIN_THRESH)

    % MIN_THRESH value gives the minimam number of occurrences of the majority
    % class
    MIN_THRESH = 2;

    % Gives the size of cut point array "j"
    max_size = size(j, 2);

    % Gives the maximum value in cut point array "j"
    max_val = max(j); 

    for i = 1:max_val;
        for k = 1:max_size
            no = 0; yes = 0;
            while(j(k) ~= i)
                k = k+1;
                if(k > max_size)
                    break;
                end
            end
            if (k <= max_size)
                while(j(k) == i)
                    if (check{k,2} == 0)
                        no = no+1;
                    else if (check{k,2} == 1)
                            yes = yes+1;
                        end
                    end
                    k = k+1;
                    if(k > max_size)
                        break;
                    end
                end
            end
            if (yes > no)
                M = 1; count = yes;
            else if (yes < no)
                    M = 0; count = no;
                else
                    M = 1; count = yes;
                end
            end
            if (count < MIN_THRESH)
                for l=k:max_size
                    j(l) = j(l)-1;
                    l = l+1;
                end
                max_val = max(j);
            else
                max_val = max(j);
            end
            break;
        end
    end


    %% Cut point removal by condition 2 (JOIN_THRESH)

    % JOIN_THRESH gives the minimum value needed to fuse two intervals.
    JOIN_THRESH = .85;


    max_size = size(j, 2);
    max_val = max(j);        

    for i =1:(max_val-1)
        if(1 ~= max_size)
            no = 0; yes = 0;
            for k = 1:max_size
                while(j(k) ~= i)
                    k = k+1;
                    if(k > max_size)
                        break;
                    end
                end
                if (k < max_size)
                    while(j(k) == i)
                        if (check{k,2} == 0)
                            no = no+1;
                        else if (check{k,2} == 1)
                                yes = yes+1;
                            end
                        end
                        k = k+1;
                        if (k > max_size)
                            break;
                        end
                    end
                end
                if (yes > no)
                    M = 1; count = yes;
                else if (yes < no)
                        M = 0; count = no;
                    else
                        M = 1; count = yes;
                    end
                end
                total = yes+no;

                m = i+1;
                no = 0; yes = 0;
                for l=1:max_size
                    while(j(l) ~= m)
                        l = l+1;
                        if(l > max_size)
                            break;
                        end
                    end
                    if(l <max_size)
                        while(j(l) == m)
                            if (check{l,2} == 0)
                                no = no+1;
                            else if (check{l,2} == 1)
                                    yes = yes+1;
                                end
                            end
                            l = l+1;
                            if(l > max_size)
                                break;
                            end
                        end
                    else
                        break;
                    end
                    if (yes > no)
                        N = 1; count_next = yes;
                    else
                        N = 0; count_next = no;
                    end
                    total_next = yes+no;

                    if (M==N)
                        val = count/total;
                        val_next = count_next/total_next;
                        if ((val >= JOIN_THRESH) && (val_next >= JOIN_THRESH))
                            for p = k:max_size
                                j(p) = j(p)-1;
                            end
                        end
                        max_val = max(j);
                    end
                    break;
                end
                break;
            end
        else
            break;
        end
    end
    
    % Data base of cut points of every feature
    cutpoint_db(feature,:) = j; 
    
    % Count the number of cutpoints in every feature and store them in no_of_feature array
    count_cutpoints = 0;
    for i = 1:total_image-1
        if(j(i) == j(i+1))
            continue;
        else
            count_cutpoints = count_cutpoints+1;
        end
    end
    no_of_feature(feature) = count_cutpoints; 
    no_of_feature(feature, 2) = feature;
    
    % Changing "check" cell back to matrix format because the values from variable "value" is taken in the matrix format
    check = cell2mat(check);
end


%% Sorting the number of cutpoints of each vector
no_of_feature = sortrows(no_of_feature);


%% Selecting the feature using feature reduction
feature_select = round((1 - feature_reduction) * total_feature);


%% Delete features from feature and cutpoint array
for i = feature_select+1:feature
    no_of_feature(feature_select+1,:) = [];
end


%% Feature Deletion from Processed feature vector 
cutpoint_db = transpose(cutpoint_db);
row = zeros(1, total_feature);
cut_row1 = zeros(1, total_feature);
row1 = zeros(1, feature_select);
cut_row = zeros(1, feature_select);
cutpoint_select = zeros(total_image, feature_select);
no_of_feature = sortrows(no_of_feature, 2);
% Keep on doing for each image
for i=1:total_image
    % Copy each row of value db and store it into row variable
    row = value{i,2};
    % Copy each row of cutpoint db and store it into cut_row1 variable
    cut_row1 =  cutpoint_db(i,:);
    % Keep on doing till all the feature to be selected are over
    for j=1:feature_select
        % Select only those features which are present in no_of_feature
        % "feature number" field and store it in variable row1
        row1(1,j) = row(1, no_of_feature(j,2));
        % Select the cutpoints of only those features which are present in
        % no_of_feature "feature number" field and store it in variable cut_row
        cut_row(1,j) = cut_row1(1, no_of_feature(j,2));
    end
    % Copy the selcted values
    value{i,2} = row1;
    cutpoint_select(i,:) = cut_row;
end


%% Data set form the "value" cell array
% Copying each row of each image to the data set including the classes also
data_set = zeros(total_image, feature_select+1);
for i = 1:total_image
    for k = 1:feature_select
        data_set(i,k) = value{i,2}(k);
    end
    data_set(i,feature_select+1) = value{i,3};
end


%% Creating Group table
% Group table contain the interval value and its corresponding discretize value.
% Its structure is (min interval, max interval, assigned value).
% It is used to create pre-processed data which will be given input to the
% associative classifier.
a = zeros(total_image,1);
b = zeros(total_image,1);
% It is a value given to each interval
index_val = 1;
for feature = 1:feature_select
    a = data_set(:,feature);
    a = sort(a);
    b = cutpoint_select(:,feature);
    count = 1;
    
    class_table = [];
    for i = 1:total_image-1
        if(b(i) ~= b(i+1))
            if(i == 1)
                class_table(count,1) = a(i);
                class_table(count,2) = a(i);
                class_table(count,3) = index_val;
                count = count+1;
                index_val = index_val+1;
                class_table(count,1) = a(i+1);
            elseif(i == total_image-1)
                class_table(count,2) = a(i);
                class_table(count,3) = index_val;
                count = count+1;
                index_val = index_val+1;
                class_table(count,1) = a(i+1);
                class_table(count,2) = a(i+1);
                class_table(count,3) = index_val;
                count = count+1;
                index_val = index_val+1;
            else
                class_table(count,2) = a(i);
                class_table(count,3) = index_val;
                count = count+1;
                index_val = index_val+1;
                class_table(count,1) = a(i+1);
            end
        else
            if(i == 1)
                class_table(count,1) = a(i);
            end
            if(i == total_image-1)
                class_table(count,2) = a(i+1);
                class_table(count,3) = index_val;
                count = count+1;
                index_val = index_val+1;
            end                 
        end
    end
    % Segregate classes of each feature
    diff_class{feature,1} = class_table;
end


% % Grouping table for Class (benign 0, malignant 1)
% Grouping is given to the class labels seperately.
count = feature_select+1;
diff_class{count,1}(1,1) = 0;
diff_class{count,1}(1,2) = 0;
diff_class{count,1}(1,3) = index_val;
index_val = index_val+1;
diff_class{count,1}(2,1) = 1;
diff_class{count,1}(2,2) = 1;
diff_class{count,1}(2,3) = index_val;


%% Substituting the values to data set(data pre processing)
for i = 1:feature_select+1
    [m n] = size(diff_class{i,1});
    for j = 1:total_image
        for k = 1:m
            if((data_set(j,i)>=diff_class{i,1}(k,1)) && (data_set(j,i)<=diff_class{i,1}(k,2)))
                data_set(j,i) = diff_class{i,1}(k,3);
                break;
            end
        end
    end
end


%% Generating the testing.txt
data_set = int32(data_set);
[m n] = size(data_set);
fileID = fopen('testing.txt','wt');
for i = 1:m
    for j = 1:n
        fprintf(fileID,'%d ',data_set(i,j));
    end
    fprintf(fileID,'\n');
end
fclose(fileID);

