#!/usr/bin/env bash

#Type can be GERMLINE,SOMATIC, SOMATIC_PAIRED
project_name=$1

genome="hg38"

file_regexp_somatic_paired="(.*)__(.*)_somatic_vep.vcf.gz$"

somatic_sample_vcf_file_pattern="{sample}_somatic.vep.vcf.gz"
file_regexp_somatic="${somatic_sample_vcf_file_pattern/\{sample\}/(.*)}$"

germline_sample_vcf_file_pattern="{sample}_germline.vep.vcf.gz"
file_regexp_germline="${somatic_sample_vcf_file_pattern/\{sample\}/(.*)}$"

filename_germline_cohort="${project_name}_cohort.vep.vcf.gz"
split_cohort_vcf_file_pattern="{sample}.vcf.gz"

coverage_file_pattern="{sample}_non_callable_regions.tsv"
bam_file_pattern="{sample}.bam"
coverage_track_pattern="{sample}_callable_loci.bb"

username="$DART_LOADER_USER"
password="$DART_LOADER_PASSWORD"
usergroup="$DART_LOADER_GROUP"

dart_server="${dart.web.server.base.url}"
sercaver_protocol="${dart.web.server.protocol}"

chromosomes="chr1,chr2,chr3,chr4,chr5,chr6,chr7,chr8,chr9,chr10,chr11,chr12,chr13,chr14,chr15,chr16,chr17,chr18,chr19,chr20,chr21,chr22,chrX,chrY,chrM"

#Environment setup
loader_path=$(dirname $0)
base_path=$(dirname $(dirname ${loader_path}))
static_content_folder="${base_path}/static-content/"
jar_name="DART-loader.jar"
main_class="org.open.medgen.dart.loader.job.VCFLoader"

echo ${static_content_folder}
#-------------------------------------------------------------------------------

loadFile ()
{
    options=" -type "${vcf_type}
    options=${options}" -config ${config_file}"
    options=${options}" -VCF ${vcf_file}"
    options=${options}" -group ${usergroup}"
    options=${options}" -server "${username}":"${password}"@"${dart_server}
    options=${options}" -chrom "${chromosomes}
    options=${options}" -staticContentBase "${static_content_folder}

    classpath=${loader_path}"/"${jar_name}

    java_opt="-Xmx4000m"
    java_opt=${java_opt}" -cp "${classpath}

    echo ""
    echo "-------------LOADING ${vcf_file}-----------------------"
    echo "JAVA PARAMS: "${java_opt}
    echo "OPTS: "${options}
    echo ""

    java ${java_opt} ${main_class} ${options}
}


public_url="local://"${project_name}"/"${genome}

file_base_folder="${static_content_folder}/${project_name}/${genome}/vcf"

for local_file in $(ls ${file_base_folder});do
	if [[ $local_file =~ ${file_regexp_somatic_paired} ]];then
		vcf_type="SOMATIC"
		
		normal_sample_name="${BASH_REMATCH[1]}"
		tumor_sample_name="${BASH_REMATCH[2]}"
		
		vcf_file_name=${local_file}
		coverage_file_name="${coverage_file_pattern/\{sample\}/${tumor_sample_name}}"
		
		vcf_track_url=${public_url}"/vcf/"${vcf_file_name}
		
		bam_file_name="${bam_file_pattern/\{sample\}/${tumor_sample_name}}"
		bam_track_url=${public_url}"/bam/"${bam_file_name}
		
		coverage_track_file_name="${coverage_track_pattern/\{sample\}/${tumor_sample_name}}"
		coverage_track_url=${public_url}"/bam/"${coverage_track_file_name}
		
		vcf_file="${file_base_folder}/${vcf_file_name}"
		coverage_file="${file_base_folder}/../bam/${coverage_file_name}"
		
		config_file="./job_config_${project_name}_${vcf_type_selector}_${tumor_sample_name}.tsv"
		echo -e "SAMPLE\tSAMPLE_ALIAS\tINCLUDE\tVCF_URL\tBAM_URL\tCOVERAGE_TRACK_URL\tCOVERAGE_FILE" > ${config_file}
		echo -e "TUMOR\t${tumor_sample_name}\tT\t${vcf_track_url}\t${bam_track_url}\t${coverage_track_url}\t${coverage_file}" >> ${config_file}
		
		loadFile
	elif [[ $local_file =~ ${file_regexp_somatic} ]];then
		vcf_type="SOMATIC"
		
		sample_name="${BASH_REMATCH[1]}"
		
		vcf_file="${file_base_folder}/${local_file}"
		coverage_file="${file_base_folder}/../bam/${coverage_file_pattern}"
		
		vcf_track_url=${public_url}"/vcf/"${somatic_sample_vcf_file_pattern}
		bam_track_url=${public_url}"/bam/"${bam_file_pattern}
		coverage_track_url=${public_url}"/bam/"${coverage_track_pattern}
		
		config_file="./job_config_${project_name}_${vcf_type_selector}_${sample_name}.tsv"
		echo -e "SAMPLE\tSAMPLE_ALIAS\tINCLUDE\tVCF_URL\tBAM_URL\tCOVERAGE_TRACK_URL\tCOVERAGE_FILE" > ${config_file}
		echo -e "*\t\tT\t${vcf_track_url}\t${bam_track_url}\t${coverage_track_url}\t${coverage_file}" >> ${config_file}
		
		loadFile
	elif [[ $local_file =~ ${file_regexp_germline} ]];then
		vcf_type="GERMLINE"
		
		sample_name="${BASH_REMATCH[1]}"
		
		vcf_file="${file_base_folder}/${local_file}"
		coverage_file="${file_base_folder}/../bam/${coverage_file_pattern}"
		
		vcf_track_url=${public_url}"/vcf/"${germline_sample_vcf_file_pattern}
		bam_track_url=${public_url}"/bam/"${bam_file_pattern}
		coverage_track_url=${public_url}"/bam/"${coverage_track_pattern}
		
		config_file="./job_config_${project_name}_${vcf_type_selector}_${sample_name}.tsv"
		echo -e "SAMPLE\tSAMPLE_ALIAS\tINCLUDE\tVCF_URL\tBAM_URL\tCOVERAGE_TRACK_URL\tCOVERAGE_FILE" > ${config_file}
		echo -e "*\t\tT\t${vcf_track_url}\t${bam_track_url}\t${coverage_track_url}\t${coverage_file}" >> ${config_file}
		
		loadFile
	elif [[ $local_file == ${filename_germline_cohort} ]];then
		vcf_type="GERMLINE"
			
		vcf_file="${file_base_folder}/${local_file}"
		coverage_file="${file_base_folder}/../bam/${coverage_file_pattern}"
		
		vcf_track_url=${public_url}"/vcf/"${split_cohort_vcf_file_pattern}
		bam_track_url=${public_url}"/bam/"${bam_file_pattern}
		coverage_track_url=${public_url}"/bam/"${coverage_track_pattern}
		
		config_file="./job_config_${project_name}_${vcf_type_selector}_COHORT.tsv"
		echo -e "SAMPLE\tSAMPLE_ALIAS\tINCLUDE\tVCF_URL\tBAM_URL\tCOVERAGE_TRACK_URL\tCOVERAGE_FILE" > ${config_file}
		echo -e "*\t\tT\t${vcf_track_url}\t${bam_track_url}\t${coverage_track_url}\t${coverage_file}" >> ${config_file}
		
		loadFile
	fi
done
  
 